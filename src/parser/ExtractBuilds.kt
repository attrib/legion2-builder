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
                            build.upgradeFighter(old, newUnitDef)
                        } else {
                            build.addFighter(newUnitDef)
                        }
                        new.position = pos
                        field[pos] = new
                    }
                }
                untouchedUnits.values.forEach { unit->
                    build.sellFighter(unit)
                }
                build.levelIncrease()
            }
            build
        })
/*
                val current = playerWave.buildings.entities.map { it.attributes["Type"]!! } + (1..playerWave.end.workers).map { "worker_unit_id" }
                val diffAdded = current.toMutableList()
                val diffRemoved = last.toMutableList()
                last.forEach {
                    diffAdded.remove(it)
                }
                current.forEach {
                    diffRemoved.remove(it)
                }
                last = current.toMutableList()

                val value = current.sumBy { gameData.unitsMap[it]!!.totalValue }


                var text = emptyList<String>()
                var gold = 0
                diffAdded.forEach { added ->
                    val unitAdded = gameData.unitsMap[added]!!
                    if (unitAdded.upgradesFrom != null) {
                        val unitUpgraded = gameData.unitsMap[unitAdded.upgradesFrom]!!
                        if (diffRemoved.contains(unitUpgraded.id)) {
                            gold += unitAdded.goldCost
                            text += "${unitUpgraded.name} -> ${unitAdded.name}"
                            build.addFighter(unitAdded)
                        } else {
                            gold += unitUpgraded.goldCost
                            gold += unitAdded.goldCost
                            text += "${unitAdded.name} (${unitUpgraded.name})*"
                            build.addFighter(unitAdded)
                        }
                    } else {
                        gold += unitAdded.goldCost
                        text += unitAdded.name

                        build.addFighter(unitAdded)
                    }
                }
*/

        return ReplayResult(playerBuilds)
    }
}