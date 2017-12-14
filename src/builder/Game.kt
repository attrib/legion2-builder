package builder

import builder.data.Global

class Game {

    val legions: MutableMap<Legion, builder.data.Legion> = mutableMapOf()
    val mercenaries: MutableMap<String, UnitDef> = mutableMapOf()
    val creatures: MutableMap<String, UnitDef> = mutableMapOf()
    val globals: MutableMap<String, Global> = mutableMapOf()
    val waves: MutableMap<Int, WaveDef> = mutableMapOf()

    init {
        globals.put(global.id, builder.data.Global(global))
        Legion.values().forEach {
            legions.put(it, builder.data.Legion(it.toString(), "", true))
        }
        units.forEach {
            when(it.unitClass) {
                UnitClass.Fighter -> legions[it.legion]!!.fighters.put(it.id, it)
                UnitClass.Creature -> creatures.put(it.id, it)
                UnitClass.Mercenary -> mercenaries.put(it.id, it)
                UnitClass.Worker -> {
                    legions.forEach { (_, legion) ->
                        legion.fighters.put(it.id, it)
                    }
                }
                else -> {}
            }
        }

        wavesDef.forEach {
            waves.put(it.levelNum, it)
        }
    }

    fun getWaveCreaturesDef(level: Int): List<UnitDef> {
        val creatureDefs = mutableListOf<UnitDef>()
        val wave = waves[level]!!
        (0 until wave.amount).forEach { creatureDefs.add(creatures[wave.unit]!!) }
        if (wave.amount2 > 0) {
            (0 until wave.amount2).forEach { creatureDefs.add(creatures[wave.unit2]!!) }
        }
        return creatureDefs
    }

}