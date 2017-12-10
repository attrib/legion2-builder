package builder

import builder.data.Unit

class Lane {

    private val fighters: MutableList<Unit> = mutableListOf()
    private val mercenaries: MutableList<Unit> = mutableListOf()

    fun getTotalHp(level: Int): Int {
        return fighters.sumBy { if (it.buildLevel!! <= level) it.hp else 0 }
    }

    fun getTotalDps(level: Int): Double {
        return fighters.sumByDouble { if (it.buildLevel!! <= level) it.dps else 0.0 }
    }

    fun getCosts(level: Int): Int {
        return fighters.sumBy { if (it.buildLevel!! <= level) it.totalvalue ?: 0 else 0 }
    }

    fun getFoodCosts(level: Int): Int {
        return fighters.sumBy { if (it.buildLevel!! <= level) it.totalfood ?: 0 else 0 }
    }

    fun getIncome(level: Int): Int {
        return mercenaries.sumBy { if (it.buildLevel!! <= level) it.incomebonus ?: 0 else 0 }
    }

    fun getFighters(level: Int): MutableMap<Int, Unit> {
        val fighterByLevel: MutableMap<Int, Unit> = mutableMapOf()
        fighters.filter { it.buildLevel!! <= level }
                .forEachIndexed { key, value -> fighterByLevel.put(key, value) }
        return fighterByLevel
    }

    fun addFighter(unit: Unit, level: Int) {
        val newUnit = unit.copy()
        newUnit.buildLevel = level
        fighters.add(newUnit)
    }

    fun removeFighter(index: Int) {
        fighters.removeAt(index)
    }

    fun getMerchenaries(level: Int): MutableMap<Int, Unit> {
        val mercenariesByLevel: MutableMap<Int, Unit> = mutableMapOf()
        mercenaries.filter { it.buildLevel!! == level }
                .forEachIndexed { key, value -> mercenariesByLevel.put(key, value) }
        return mercenariesByLevel
    }

    fun addMerchenary(unit: Unit, level: Int) {
        val newUnit = unit.copy()
        newUnit.buildLevel = level
        mercenaries.add(newUnit)
    }

    fun removeMerchenary(index: Int) {
        mercenaries.removeAt(index)
    }

}