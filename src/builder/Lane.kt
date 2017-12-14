package builder

import builder.data.Unit

class Lane {

    private val fighters: MutableList<Unit> = mutableListOf()
    private val mercenaries: MutableList<Unit> = mutableListOf()

    fun getWorkerCount(level: Int):Int {
        return 1 + fighters.filter { it.buildLevel!! <= level }
                .filter { it.def.unitClass == UnitClass.Worker }
                .size
    }

    fun getTotalHp(level: Int): Int {
        return getFighters(level).sumBy { if (it.buildLevel!! <= level) it.def.hitpoints else 0 }
    }

    fun getTotalDps(level: Int): Double {
        return getFighters(level).sumByDouble { if (it.buildLevel!! <= level) it.def.dmgBase * it.def.attackSpeed else 0.0 }
    }

    fun getCosts(level: Int): Int {
        return fighters.sumBy { if (it.buildLevel!! <= level) it.def.totalValue else 0 }
    }

    fun getFoodCosts(level: Int): Int {
        return fighters.sumBy { if (it.buildLevel!! <= level) it.def.totalFood else 0 }
    }

    fun getIncome(level: Int): Int {
        return mercenaries.sumBy { if (it.buildLevel!! <= level) it.def.incomeBonus else 0 }
    }

    fun getFighters(level: Int, includeWorkers: Boolean = false): List<Unit> {
        return fighters.filter { it.buildLevel!! <= level }
                .filter { ( includeWorkers && it.buildLevel!! == level ) || it.def.unitClass != UnitClass.Worker }
    }

    fun getFighterDef(level: Int, includeWorkers: Boolean = false): List<UnitDef> {
        return getFighters(level, includeWorkers).map { it.def }
    }

    fun addFighter(unit: UnitDef, level: Int) {
        val newUnit = Unit(unit)
        newUnit.buildLevel = level
        fighters.add(newUnit)
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