package builder.data

import builder.UnitClass
import builder.UnitDef
import parser.Position

fun UnitDef.isEnabled(): Boolean {
        return !this.id.startsWith("test")
}

class Unit(val def: UnitDef) {
        var buildLevel: Int? = null
        var upgradedLevel: Int? = null
        var soldLevel: Int? = null
    var position = Position(0, 0)
}

class Units(list: List<Unit>) : List<Unit> by list {
    fun forLevel(level: Int) = Units(filter { it.buildLevel == level })
    fun upToLevel(level: Int) = Units(filter { it.buildLevel!! <= level })
    fun sold(level: Int) = Units(filter { it.soldLevel != null && it.soldLevel!! <= level })
    fun notSold(level: Int) = Units(filter { it.soldLevel == null || it.soldLevel!! > level })
    fun notUpgraded(level: Int) = Units(filter { it.upgradedLevel == null || it.upgradedLevel!! > level })

    fun totalHp() = sumBy { it.def.hitpoints }
    fun totalDps() = sumByDouble { it.def.dmgBase / it.def.attackSpeed }
    fun totalValue() = sumBy { it.def.totalValue }
    fun totalFood() = sumBy { it.def.totalFood }
    fun totalIncome() = sumBy { it.def.incomeBonus }

    fun ownCreatures() = Units(filter { it.def.unitClass == UnitClass.Fighter || it.def.unitClass == UnitClass.Worker })
    fun fighers() = Units(filter { it.def.unitClass == UnitClass.Fighter })
    fun worker() = Units(filter { it.def.unitClass == UnitClass.Worker })
    fun mercenaries() = Units(filter { it.def.unitClass == UnitClass.Mercenary })
}