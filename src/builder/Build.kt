package builder

import app.format
import builder.data.*

class Build(val waves: Map<Int, Wave>, val global: Global) {

    var legionId: String = ""
    var legion: Legion? = null
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
                reward += waves[i]!!.totalreward
                reward += lane.getIncome(i)
            }
        }
        return reward;
    }

    fun levelIncrease() {
        if (waves.containsKey(currentLevel + 1)) {
            currentLevel++
        }
    }

    fun levelDecrease() {
        if (waves.containsKey(currentLevel - 1)) {
            currentLevel--
        }
    }

    fun getWorkerCount(): Int {
        return lane.getWorkerCount(currentLevel)
    }

    fun getFighters(includeWorkers: Boolean = false): MutableMap<Int, Unit> {
        return lane.getFighters(currentLevel, includeWorkers)
    }

    fun addFighter(unit: Unit) {
        lane.addFighter(unit, currentLevel)
    }

    fun removeFighter(index: Int) {
        lane.removeFighter(index)
    }

    fun getMerchenaries(): MutableMap<Int, Unit> {
        return lane.getMerchenaries(currentLevel)
    }

    fun addMerchenary(unit: Unit) {
        lane.addMerchenary(unit, currentLevel)
    }

    fun removeMerchenary(index: Int) {
        lane.removeMerchenary(index)
    }

    fun survivability(wave: Wave): String {
        val creatures = mutableListOf<Unit>()
        (0 until wave.amount).forEach { creatures.add(wave.creatures.first()) }
        if (wave.amount2 > 0) {
            (0 until wave.amount2).forEach { creatures.add(wave.creatures.last()) }
        }
        val calc = BattleCalc(global, lane
                .getFighters(currentLevel).values
                .toList(), creatures,
                {
                    var units = it.filter { it.unit.attackMode === "Melee" }
                    if (units.isEmpty()) {
                        units = it
                    }
                    units.sortedBy { it.hitpoints }
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
            val possibility = leftHpCreatures / wave.totalHp * 100
            "High leak probability (${possibility.format(2)}% remaining creatures hp)"
        }
    }

    fun getResistance(testUnit: Unit?): Resistance {
        return Resistance(lane.getFighters(currentLevel).values.toList(), global, testUnit)
    }
}