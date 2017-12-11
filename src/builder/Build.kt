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
    val resistance get() = lane.getResistance(currentLevel)

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

    fun getFighters(): MutableMap<Int, Unit> {
        return lane.getFighters(currentLevel)
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
        val calc = BattleCalc(global, lane
                .getFighters(currentLevel).values
                .toList(), (0 until wave.amount).map { wave.creatures.first() },
                { it.shuffled().first() }
        )
        val result = calc.calc()

        val leftHp = result.hpA()
        return if (leftHp > 0) {
            val possibility = (leftHp / totalHp * 100)
            when {
                possibility < 25 -> "Medium leak probability"
                else -> "Low leak probability"
            } + " (${possibility.format(2)}% remaining hp)"
        }
        else {
            val leftHpCreatures = result.hpB()
            val possibility = leftHpCreatures / wave.totalHp * 100
            "High leak probability (${possibility.format(2)}% remaining hp)"
        }
    }
}