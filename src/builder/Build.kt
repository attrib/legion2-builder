package builder

import app.format
import builder.data.Legion
import builder.data.Unit
import builder.data.Wave
import kotlin.math.roundToInt

class Build(val waves: Map<Int, Wave>) {

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
            for (i in 1 until (currentLevel - 1)) {
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
        val secondsToKill = wave.totalHp / totalDps
        val leftHp = totalHp - (secondsToKill * wave.totalDps)
        var result = ""
        if (leftHp > 0) {
            val possibility = (leftHp / totalHp * 100)
            result = when {
                possibility < 25 -> "Medium leak probability"
                else -> "Low leak probability"
            }
            result += " (" + possibility.format(2) + "%)"
        }
        else {
            val secondsToBeKilled = totalHp / wave.totalDps
            val leftHpCreatures = wave.totalHp - (secondsToBeKilled * totalDps)
            val creature = wave.creatures.first();
            val leftUnits = kotlin.math.ceil(leftHpCreatures / creature.hp).roundToInt().toString()
            result = "High leak probability (leaking >$leftUnits units)"
        }
        return result
    }
}