package builder

import app.format
import builder.data.BattleCalc
import builder.data.Resistance
import builder.data.Result
import builder.data.Unit

class Build(val game: Game, val global: Global) {

    var legion: Legion? = null
    var legionId: String? = null
    private val lane: Lane = Lane()
    var currentLevel = 1

    val costs get() = lane.getCosts(currentLevel)
    val foodCosts get() = lane.getFoodCosts(currentLevel)
    val available get() = reward() - costs
    val income get() = lane.getIncome(currentLevel)
    val totalHp get() = lane.getTotalHp(currentLevel)
    val totalDps get() = lane.getTotalDps(currentLevel)

    private fun reward(): Int {
        var reward = 250
        if (currentLevel > 1) {
            for (i in 1 until (currentLevel)) {
                reward += game.data.waves[i]!!.totalReward
                reward += lane.getIncome(i)
            }
        }
        return reward
    }

    fun levelIncrease() {
        if (currentLevel < game.data.waves.size) {
            currentLevel++
        }
    }

    fun levelDecrease() {
        if (currentLevel > 0) {
            currentLevel--
        }
    }

    fun getWorkerCount(): Int {
        return lane.getWorkerCount(currentLevel)
    }

    fun getFighters(includeWorkers: Boolean = false): List<Unit> {
        return lane.getFighters(currentLevel, includeWorkers)
    }

    fun addFighter(unit: UnitDef) {
        lane.addFighter(unit, currentLevel)
    }

    fun removeFighter(unit: Unit) {
        lane.removeFighter(unit)
    }

    fun getMerchenaries(): List<Unit> {
        return lane.getMerchenaries(currentLevel)
    }

    fun addMerchenary(unit: UnitDef) {
        lane.addMerchenary(unit, currentLevel)
    }

    fun removeMerchenary(unit: Unit) {
        lane.removeMerchenary(unit)
    }

    fun survivability(creatures: List<UnitDef>): String {
        val calc = BattleCalc(game.data.global, lane.getFighterDef(currentLevel), creatures,
                {
                    var units = it.filter { it.unit.attackMode === AttackMode.Melee }
                    if (units.isEmpty()) {
                        units = it
                    }
                    units = units.sortedBy { it.hitpoints }
                    units.first()
                }
        )
        val results: MutableList<Result> = mutableListOf()
        for (i in 0..1) {
            results.add(calc.calc())
        }

        val leftHp = results.sumByDouble { it.hpA() } / results.size
        return if (leftHp > 0) {
            val possibility = (leftHp / totalHp * 100)
            when {
                possibility < 25 -> "Medium leak probability"
                else -> "Low leak probability"
            } + " (${possibility.format(2)}% remaining fighter hp)"
        } else {
            val leftHpCreatures = results.sumByDouble { it.hpB() } / results.size
            val possibility = leftHpCreatures / creatures.sumBy { it.hitpoints } * 100
            "High leak probability (${possibility.format(2)}% remaining creatures hp)"
        }
    }

    fun getResistance(testUnit: UnitDef?): Resistance {
        return Resistance(lane.getFighterDef(currentLevel), global, testUnit)
    }
}