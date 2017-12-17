package builder

import app.format
import builder.data.BattleCalc
import builder.data.Resistance
import builder.data.Result
import builder.data.Unit

class Build() {

    var legion: Legion? = null
    var legionId: String? = null
    private val lane: Lane = Lane()
    var currentLevel = 0

    val costs get() = lane.getCosts(currentLevel)
    val foodCosts get() = lane.getFoodCosts(currentLevel)
    val available get() = reward() - costs
    val income get() = lane.getIncome(currentLevel)
    val totalHp get() = lane.getTotalHp(currentLevel)
    val totalDps get() = lane.getTotalDps(currentLevel)

    private fun reward(): Int {
        var reward = 250
        if (currentLevel > 0) {
            for (i in 0 until (currentLevel)) {
                reward += LegionData.waves[i].totalReward
                reward += lane.getIncome(i)
            }
        }
        return reward
    }

    fun levelIncrease() {
        if (currentLevel < LegionData.waves.size - 1) {
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

    fun getFightersUnfiltered(): List<Unit> {
        return lane.getFightersUnfiltered()
    }

    fun addFighter(unit: UnitDef): Unit {
        return lane.addFighter(unit, currentLevel)
    }

    fun removeFighter(unit: Unit) {
        lane.removeFighter(unit)
    }

    fun upgradeFighter(selectedUnit: Unit, upgradeUnitDef: UnitDef): Unit {
        return lane.upgradeFighter(selectedUnit, upgradeUnitDef, currentLevel)
    }

    fun sellFighter(selectedUnit: Unit) {
        lane.sellFighter(selectedUnit, currentLevel)
    }

    fun getMerchenaries(): List<Unit> {
        return lane.getMerchenaries(currentLevel)
    }

    fun getMerchenaries(level: Int): List<Unit> {
        return lane.getMerchenaries(level)
    }

    fun addMerchenary(unit: UnitDef) {
        lane.addMerchenary(unit, currentLevel)
    }

    fun removeMerchenary(unit: Unit) {
        lane.removeMerchenary(unit)
    }

    fun survivability(creatures: List<UnitDef>): String {
        val calc = BattleCalc(LegionData.global, lane.getFighterDef(currentLevel), creatures,
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
        return Resistance(lane.getFighterDef(currentLevel), testUnit)
    }

    fun getCostsByLevel(level: Int): Int {
        return lane.getCosts(level)
    }
}