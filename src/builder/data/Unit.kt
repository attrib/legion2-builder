package builder.data

import builder.DSFactory
import builder.LegionData
import builder.UnitDef
import builder.intOrNull
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
        val index = LegionData.units.indexOf(def)
        ds.writeInt16(index)
        ds.writeInt8(buildLevel ?: -1)
        ds.writeInt8(upgradedLevel ?: -1)
        ds.writeInt8(soldLevel ?: -1)
        ds.writeInt16(position.x)
        ds.writeInt16(position.y)
    }


    companion object {
        fun load(ds:DSFactory.DataStream) : Unit {
            val index = ds.readInt16()
            val unit = Unit(LegionData.units[index])
            unit.buildLevel = intOrNull(ds.readInt8())
            unit.upgradedLevel = intOrNull(ds.readInt8())
            unit.soldLevel = intOrNull(ds.readInt8())
            unit.position = Position(ds.readInt16(), ds.readInt16())
            return unit
        }
    }
}
