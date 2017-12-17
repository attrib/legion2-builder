package builder

import builder.data.Unit
import kotlin.math.roundToInt

class Lane {

    private val fighters: MutableList<Unit> = mutableListOf()
    private val mercenaries: MutableList<Unit> = mutableListOf()

    fun getWorkerCount(level: Int):Int {
        return 1 + getFighters(level, true)
                .filter { it.def.unitClass == UnitClass.Worker }
                .size
    }

    fun getTotalHp(level: Int): Int {
        return getFighters(level).sumBy { it.def.hitpoints }
    }

    fun getTotalDps(level: Int): Double {
        return getFighters(level).sumByDouble { it.def.dmgBase * it.def.attackSpeed }
    }

    fun getCosts(level: Int): Int {
        var costs = getFighters(level, true).sumBy { it.def.totalValue }
        costs += getSoldFighter(level).sumByDouble { it.def.totalValue * 0.6 }.roundToInt()
        return costs
    }

    fun getFoodCosts(level: Int): Int {
        return getFighters(level, true).sumBy { it.def.totalFood }
    }

    fun getIncome(level: Int): Int {
        return mercenaries.sumBy { if (it.buildLevel!! <= level) it.def.incomeBonus else 0 }
    }

    fun getFighters(level: Int, includeWorkers: Boolean = false): List<Unit> {
        return fighters
                .filter { it.buildLevel!! <= level }
                .filter { it.upgradedLevel == null || it.upgradedLevel!! > level }
                .filter { it.soldLevel == null || it.soldLevel!! > level }
                .filter { ( includeWorkers && it.buildLevel!! == level ) || it.def.unitClass != UnitClass.Worker }
    }

    fun getFighterDef(level: Int, includeWorkers: Boolean = false): List<UnitDef> {
        return getFighters(level, includeWorkers).map { it.def }
    }

    fun upgradeFighter(selectedUnit: Unit, upgradeTo: UnitDef, level: Int): Unit {
        selectedUnit.upgradedLevel = level
        return addFighter(upgradeTo, level)
    }

    fun sellFighter(selectedUnit: Unit, level: Int) {
        selectedUnit.soldLevel = level
    }

    fun getSoldFighter(level: Int): List<Unit> {
        return fighters.filter { it.soldLevel != null && it.soldLevel!! <= level }
    }

    fun addFighter(unit: UnitDef, level: Int): Unit {
        val newUnit = Unit(unit)
        newUnit.buildLevel = level
        fighters.add(newUnit)
        return newUnit
    }

    fun removeFighter(unit: Unit) {
        fighters.remove(unit)
    }

    fun getMerchenaries(level: Int): List<Unit> {
        return mercenaries.filter { it.buildLevel!! == level }
    }

    fun addMerchenary(unit: UnitDef, level: Int) {
        val newUnit = Unit(unit)
        newUnit.buildLevel = level
        mercenaries.add(newUnit)
    }

    fun removeMerchenary(unit: Unit) {
        mercenaries.remove(unit)
    }

}