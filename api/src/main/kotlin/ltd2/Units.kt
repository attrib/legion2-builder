package ltd2

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerialClassDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.SerialClassDescImpl

data class Position(val x: Int, val y: Int)

fun UnitDef.isEnabled(): Boolean {
    return !this.id.startsWith("test")
}
fun intOrNull(i:Number):Int? = if( i==-1) null else i.toInt()

object UnitSerializer : KSerializer<UnitState> {
    override fun load(input: KInput): UnitState {

        val index = input.readStringValue()
        val unit = UnitState(LegionData.unitsMap[index]!!)
        unit.buildLevel = intOrNull(input.readByteValue())
        unit.upgradedLevel = intOrNull(input.readByteValue())
        unit.soldLevel = intOrNull(input.readByteValue())
        unit.position = Position(input.readShortValue().toInt(), input.readShortValue().toInt())
        return unit
    }

    override fun save(output: KOutput, obj: UnitState) {
        obj.apply {
            output.writeStringValue(def.id)
            output.writeByteValue((buildLevel ?: -1).toByte())
            output.writeByteValue((upgradedLevel ?: -1).toByte())
            output.writeByteValue((soldLevel ?: -1).toByte())
            output.writeShortValue(position.x.toShort())
            output.writeShortValue(position.y.toShort())
        }
    }

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("ltd2.UnitState")
}
class UnitState(val def: UnitDef) {
    var buildLevel: Int? = null
    var upgradedLevel: Int? = null
    var upgradedFrom: UnitState? = null
    var soldLevel: Int? = null
    var position = Position(0, 0)
}

class Units(list: List<UnitState>) : List<UnitState> by list {
    fun forLevel(level: Int) = Units(filter { it.buildLevel == level })
    fun upToLevel(level: Int) = Units(filter { it.buildLevel!! <= level })
    fun sold(level: Int) = Units(filter { it.soldLevel != null && it.soldLevel!! <= level })
    fun notSold(level: Int) = Units(filter { it.soldLevel == null || it.soldLevel!! > level })
    fun notUpgraded(level: Int) = Units(filter { it.upgradedLevel == null || it.upgradedLevel!! > level })
    fun removeWorkersNotLevel(level: Int) = Units(filter { it.def.unitClass != UnitClass.Worker || it.buildLevel == level })

    fun totalHp() = sumBy { it.def.hitpoints }
    fun totalDps() = sumByDouble { if (it.def.attackSpeed > 0) it.def.dmgBase / it.def.attackSpeed else 0.0 }
    fun totalGold() = sumBy { it.def.goldCost }
    fun totalValue() = sumBy { it.def.totalValue }
    fun totalFood() = sumBy { it.def.totalFood }
    fun totalIncome() = sumBy { it.def.incomeBonus }

    fun ownCreatures() = Units(filter { it.def.unitClass == UnitClass.Fighter || it.def.unitClass == UnitClass.Worker })
    fun fighters() = Units(filter { it.def.unitClass == UnitClass.Fighter })
    fun mercenaries() = Units(filter { it.def.unitClass == UnitClass.Mercenary })
}