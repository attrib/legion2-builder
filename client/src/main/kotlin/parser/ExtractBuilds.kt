package parser

import builder.Build
import builder.LegionData
import builder.data.Unit
import kotlin.js.RegExp


data class Position(val x: Int, val y: Int)
data class GridPosition(val x: Double, val y: Double)
data class ReplayResult(val playerBuilds: Map<String, Build>)

val POSITION_PATTERN = RegExp("^\\(([0-9\\.]+), ([0-9\\.]+)\\)$")

object ExtractBuilds {
    fun extactBuilds(logdata: LogFile): ReplayResult {
        val r = Replay(logdata)
        val game = r.build()

        val playerBuilds = game.players.filter { it.isInActiveTeam() }.associateBy({ it.name }, { player ->
            val build = Build()
            val field = mutableMapOf<Position, Unit>()
            var workers = 0
            game.waves.forEach { wave ->
                val playerWave = wave.playerWaves[player.name]!!
                val untouchedUnits = field.toMutableMap()
                playerWave.buildings.entities.forEach { es ->
                    val matcher = POSITION_PATTERN.exec(es.attributes["Position"]!!)
                    if (matcher != null) {
                        val x = (2 * matcher[1]!!.toDouble()).toInt()
                        val y = (2 * matcher[2]!!.toDouble()).toInt()
                        val pos = Position(x, y)
                        untouchedUnits.remove(pos)
                        val old = field[pos]
                        val newUnitDef = LegionData.unitsMap[es.attributes["Type"]!!]!!
                        val new = if (old != null) {
                            if( old.def.id!=newUnitDef.id ) {
                                build.upgradeFighter(old, newUnitDef)
                            } else {
                                old
                            }
                        } else {
                            build.addFighter(newUnitDef)
                        }
                        new.position = pos
                        field[pos] = new
                    }
                }
                if (playerWave.end.workers > workers) {
                    (1..playerWave.end.workers - workers).forEach {
                        build.addFighter(LegionData.unitsMap["worker_unit_id"]!!)
                    }
                    workers = playerWave.end.workers
                }
                untouchedUnits.values.forEach { unit->
                    build.sellFighter(unit)
                }
                build.levelIncrease()
            }
            build
        })

        return ReplayResult(playerBuilds)
    }
}