package builder

import builder.data.*
import kotlinext.js.Object
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Json

interface GameEventHandler {
    fun loaded()
}

class Game(gameEventHandler: GameEventHandler) {

    val legions: MutableMap<String, Legion> = mutableMapOf()
    val mercenaries: MutableMap<String, Unit> = mutableMapOf()
    val creatures: MutableMap<String, Unit> = mutableMapOf()
    val globals: MutableMap<String, Global> = mutableMapOf()
    var defaultGlobal: String = ""

    init {
        val request = XMLHttpRequest()
        request.overrideMimeType("application/json")
        request.open("GET", "LTD2_Data.json", true)
        request.onreadystatechange = {
            if (request.readyState == XMLHttpRequest.DONE && request.status == 200.toShort())  {
                val data = JSON.parse<Json>(request.responseText)
                val legionsData = data["legions"] as Json
                for (k in Object.keys(legionsData!!)) {
                    val legion = legionsData[k] as Json
                    legions.put(k, Legion(legion["name"] as String, legion["iconpath"] as String, legion["playable"] as String))
                }

                val globalData = data["globals"] as Json
                for (k in Object.keys(globalData!!)) {
                    val global = globalData[k] as Json
                    globals.put(k, Global(
                            global["name"] as String,
                            global["attackchartpierce"] as String,
                            global["attackchartnormal"] as String,
                            global["attackchartmagic"] as String,
                            global["attackchartsiege"] as String,
                            global["attackchartchaos"] as String
                    ))
                    defaultGlobal = k
                }

                val unitData = data["units"] as Json
                for (k in Object.keys(unitData!!)) {
                    val unitJson = unitData[k] as Json

                    val attackType = when(unitJson["attacktype"] as String) {
                        "Impact" -> AttackType.IMPACT
                        "Pierce" -> AttackType.PIERCE
                        "Magic" -> AttackType.MAGIC
                        "Pure" -> AttackType.PURE
                        else -> AttackType.UNKNOWN
                    }
                    val armorType = when(unitJson["armortype"] as String) {
                        "Swift" -> DefenseType.SWIFT
                        "Natural" -> DefenseType.NATURAL
                        "Fortified" -> DefenseType.FORTIFIED
                        "Arcane" -> DefenseType.ARCANE
                        "Immaterial" -> DefenseType.IMMATERIAL
                        else -> DefenseType.UNKNOWN
                    }
                    val unitClass = UnitClass.valueOf(unitJson["unitclass"] as String)

                    val unit = Unit(
                            unitJson["name"] as String,
                            mutableListOf(),
                            unitJson["description"] as String,
                            unitJson["iconpath"] as String,
                            (unitJson["hp"] as String).toInt(),
                            (unitJson["dps"] as String).toDoubleOrNull(),
                            attackType,
                            armorType,
                            unitJson["attackmode"] as String,
                            (unitJson["range"] as String).toIntOrNull(),
                            unitClass,
                            mutableListOf(),
                            mutableListOf(),
                            (unitJson["goldcost"] as String).toIntOrNull(),
                            (unitJson["foodcost"] as String).toIntOrNull(),
                            (unitJson["totalvalue"] as String).toIntOrNull(),
                            (unitJson["totalfood"] as String).toIntOrNull(),
                            (unitJson["goldvalue"] as String).toIntOrNull(),
                            (unitJson["mythiumcost"] as String).toIntOrNull(),
                            (unitJson["incomebonus"] as String).toIntOrNull(),
                            unitJson["isenabled"] as String,
                            unitJson["legion_id"] as String
                    )
                    when (unitClass) {
                        UnitClass.Creature -> creatures.put(k, unit)
                        UnitClass.Mercenary -> mercenaries.put(k, unit)
                        UnitClass.Fighter -> {
                            if (legions.containsKey(unit.legion_id)) {
                                legions[unit.legion_id]?.fighters?.put(k, unit)
                            }
                            unit.legion_id
                        }
                    }
                }

                gameEventHandler.loaded()
            }
        }
        request.send()
    }

}