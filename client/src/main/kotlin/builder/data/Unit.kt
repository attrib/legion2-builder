package builder.data
import ltd2.*
import builder.*
import parser.Position

fun UnitDef.isEnabled(): Boolean {
    return !this.id.startsWith("test")
}

class Unit(val def: UnitDef) {
    var buildLevel: Int? = null
    var upgradedLevel: Int? = null
    var soldLevel: Int? = null
    var position = Position(0, 0)

    fun save(ds:DSFactory.DataStream) {
        ds.writeUtf8WithLen(def.id)
        ds.writeInt8(buildLevel ?: -1)
        ds.writeInt8(upgradedLevel ?: -1)
        ds.writeInt8(soldLevel ?: -1)
        ds.writeInt16(position.x)
        ds.writeInt16(position.y)
    }


    companion object {
        fun load(ds:DSFactory.DataStream) : Unit {
            val index = ds.readUtf8WithLen()
            val unit = Unit(LegionData.unitsMap[index]!!)
            unit.buildLevel = intOrNull(ds.readInt8())
            unit.upgradedLevel = intOrNull(ds.readInt8())
            unit.soldLevel = intOrNull(ds.readInt8())
            unit.position = Position(ds.readInt16(), ds.readInt16())
            return unit
        }
    }
}

class Units(list: List<Unit>) : List<Unit> by list {
    fun forLevel(level: Int) = Units(filter { it.buildLevel == level })
    fun upToLevel(level: Int) = Units(filter { it.buildLevel!! <= level })
    fun sold(level: Int) = Units(filter { it.soldLevel != null && it.soldLevel!! <= level })
    fun notSold(level: Int) = Units(filter { it.soldLevel == null || it.soldLevel!! > level })
    fun notUpgraded(level: Int) = Units(filter { it.upgradedLevel == null || it.upgradedLevel!! > level })
    fun removeWorkersNotLevel(level: Int) = Units(filter { it.def.unitClass != UnitClass.Worker || it.buildLevel == level })

    fun totalHp() = sumBy { it.def.hitpoints }
    fun totalDps() = sumByDouble { it.def.dmgBase / it.def.attackSpeed }
    fun totalGold() = sumBy { it.def.goldCost }
    fun totalValue() = sumBy { it.def.totalValue }
    fun totalFood() = sumBy { it.def.totalFood }
    fun totalIncome() = sumBy { it.def.incomeBonus }

    fun ownCreatures() = Units(filter { it.def.unitClass == UnitClass.Fighter || it.def.unitClass == UnitClass.Worker })
    fun fighters() = Units(filter { it.def.unitClass == UnitClass.Fighter })
    fun worker() = Units(filter { it.def.unitClass == UnitClass.Worker })
    fun mercenaries() = Units(filter { it.def.unitClass == UnitClass.Mercenary })
}