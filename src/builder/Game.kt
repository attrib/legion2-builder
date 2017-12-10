package builder

import builder.data.*
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
    val waves: MutableMap<Int, Wave> = mutableMapOf()

    init {
        val request = XMLHttpRequest()
        request.overrideMimeType("application/json")
        request.open("GET", "LTD2_Data.json", true)
        request.onreadystatechange = {
            if (request.readyState == XMLHttpRequest.DONE && request.status == 200.toShort())  {
                val data = JSON.parse<Json>(request.responseText)
                val foo = LegionTD2(data)
                legions.putAll(foo.legions)
                globals.putAll(foo.globals)
                creatures.putAll(foo.units.filter { it.value.unitClass == UnitClass.Creature })
                mercenaries.putAll(foo.units.filter { it.value.unitClass == UnitClass.Mercenary })
                foo.units.filter { it.value.unitClass == UnitClass.Fighter }.forEach { e ->
                    if (legions.containsKey(e.value.legion_id)) {
                        legions[e.value.legion_id]!!.fighters[e.key] = e.value
                    }
                }
                foo.units.filter { it.value.unitClass == UnitClass.Worker }.forEach { e ->
                    legions.forEach { it.value.fighters[e.key] = e.value }
                }

                for ((key, wave) in foo.waves) {
                    if (creatures.containsKey(wave.unit_id)) {
                        val creature = creatures[wave.unit_id]!!
                        creature.amount = wave.amount
                        wave.creatures.add(creature)
                    }
                    if (creatures.containsKey(wave.spellunit2_id)) {
                        val creature = creatures[wave.spellunit2_id]!!
                        creature.amount = wave.amount2
                        wave.creatures.add(creature)
                    }
                    waves.put(wave.levelnum, wave)
                }

                gameEventHandler.loaded()
            }
        }
        request.send()
    }

    fun getWave(level: Int): Wave? {
        return waves[level]
    }

}