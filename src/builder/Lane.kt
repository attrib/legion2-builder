package builder

import builder.data.Unit
import builder.data.Units
import kotlin.math.roundToInt

class Lane {
    val list = mutableListOf<Unit>()
    var units = Units(list)

    fun save(ds:DSFactory.DataStream) {
        ds.writeInt16(list.size)
        list.forEach {
            it.save(ds)
        }
    }

    fun load(ds:DSFactory.DataStream) {
        list.clear()

        val len = ds.readInt16()
        (0 until len).forEach {
            list += Unit.load(ds)
        }
        units = Units(list)
    }

    fun getWorkerCount(level: Int):Int {
        return 1 + getFighters(level).worker().size
    }

    fun getTotalHp(level: Int): Int {
        return getFighters(level).fighters().totalHp()
    }

    fun getTotalDps(level: Int): Double {
        return getFighters(level).fighters().totalDps()
    }

    fun getCosts(level: Int): Int {
        var costs = getFighters(level).fighers().totalValue()
        costs += getSoldFighter(level).sumByDouble { it.def.totalValue * 0.6 }.roundToInt()
        return costs
    }

    fun getValue(level: Int): Int {
        return getFighters(level).fighers().totalValue()
    }

    fun getFoodCosts(level: Int): Int {
        return getFighters(level).totalFood()
    }

    fun getIncome(level: Int): Int {
        return getFighters(level).totalIncome()
    }

    fun getFighters(level: Int): Units {
        return units
                .ownCreatures()
                .upToLevel(level)
                .notSold(level)
                .notUpgraded(level)
    }

    fun getFightersUnfiltered(): Units {
        return units
    }

    fun getFighterDef(level: Int, includeWorkers: Boolean = false): List<UnitDef> {
        if (includeWorkers) {
            return getFighters(level).map { it.def }
        } else {
            return getFighters(level).fighters().map { it.def }
        }
    }

    fun upgradeFighter(selectedUnit: Unit, upgradeTo: UnitDef, level: Int): Unit {
        selectedUnit.upgradedLevel = level
        return addFighter(upgradeTo, level)
    }

    fun sellFighter(selectedUnit: Unit, level: Int) {
        selectedUnit.soldLevel = level
    }

    fun getSoldFighter(level: Int): Units {
        return units.sold(level)
    }

    fun addFighter(unit: UnitDef, level: Int): Unit {
        val newUnit = Unit(unit)
        newUnit.buildLevel = level
        list.add(newUnit)
        units = Units(list)
        return newUnit
    }

    fun removeFighter(unit: Unit) {
        list.remove(unit)
        units = Units(list)
    }

    fun getMerchenaries(level: Int): Units {
        return units.mercenaries().forLevel(level)
    }

    fun addMerchenary(unit: UnitDef, level: Int) {
        val newUnit = Unit(unit)
        newUnit.buildLevel = level
        list.add(newUnit)
        units = Units(list)
    }

    fun removeMerchenary(unit: Unit) {
        list.remove(unit)
        units = Units(list)
    }

}