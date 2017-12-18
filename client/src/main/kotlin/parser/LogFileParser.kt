package parser

import kotlin.js.RegExp


data class Entry(val time: Int, val value: String)
data class Attribute(val id: String, val timeLine: List<Entry>) {
    fun at(time: Int): Entry? {
        val x = timeLine.indexOfFirst { it.time > time }
        if (x > 0) {
            return timeLine[x - 1]
        } else if (x == 0) {
            return timeLine[0]
        } else {
            return timeLine.lastOrNull()
        }
    }
}

data class Entity(val id: Int, val attributes: List<Attribute>) {
    val map: Map<String, Attribute>

    init {
        map = mutableMapOf()
        attributes.forEach {
            map[it.id] = it
        }
    }

    fun attr(id: String): List<Entry>? {
        return map[id]?.timeLine
    }

    fun attr(id: String, time: Int): String? {
        return map[id]?.at(time)?.value
    }
}

data class TimeEntry(val time: Int, val affectedUnits: List<Int>)
data class LogFile(val units: Map<Int, Entity>, val players: Map<Int, Entity>, val timeLine: List<TimeEntry>)

val UNIT_PATTERN = RegExp("^(Unit|Player) ([0-9]+)$")
val ATTR_PATTERN = RegExp("^(.+):$")
val ENTRY_PATTERN = RegExp("^   ([0-9]+): (.*)$")

class LogParser(val file: String) {
    val timeEvents = mutableMapOf<Int, MutableSet<Int>>()
    fun parse(): LogFile {
        timeEvents.clear()
        var unitPlayer = ""
        var currentUnit = -1
        var currentAttr = ""
        var map = emptyList<Entry>()
        var attrs = emptyList<Attribute>()
        val entities = mutableMapOf<Int, Entity>()
        val players = mutableMapOf<Int, Entity>()
        val lines = file.split("\r\n")
        lines.forEach { line ->
            var matcher = UNIT_PATTERN.exec(line)
            if (matcher != null) {
                if (currentAttr != "") {
                    attrs += Attribute(currentAttr, map)
                }
                if (currentUnit != -1) {
                    if (unitPlayer == "Unit") {
                        entities[currentUnit] = Entity(currentUnit, attrs)
                    } else {
                        players[currentUnit] = Entity(currentUnit, attrs)
                    }
                    attrs = emptyList()
                }
                unitPlayer = matcher[1]!!
                currentUnit = matcher[2]!!.toInt()
                currentAttr = ""
            }
            matcher = ATTR_PATTERN.exec(line)
            if (matcher != null) {
                if (currentAttr != "") {
                    attrs += Attribute(currentAttr, map)
                }
                currentAttr = matcher[1]!!
                map = emptyList()

            }
            matcher = ENTRY_PATTERN.exec(line)
            if (matcher != null) {
                val time = matcher[1]!!.toInt()
                addTimeEvent(time, currentUnit)
                val value = matcher[2]!!
                map += Entry(time, value)
            }
        }
        if (currentAttr != "") {
            attrs += Attribute(currentAttr, map)
        }
        if (currentUnit != -1) {
            if (unitPlayer == "Unit") {
                entities[currentUnit] = Entity(currentUnit, attrs)
            } else {
                players[currentUnit] = Entity(currentUnit, attrs)
            }
        }
        return LogFile(entities, players, timeEvents.keys.sorted().map { TimeEntry(it, timeEvents[it]!!.toList()) })
    }

    fun addTimeEvent(time: Int, id: Int) {
        val timeLine = timeEvents[time] ?: mutableSetOf()
        timeLine += id
        timeEvents[time] = timeLine
    }
}
