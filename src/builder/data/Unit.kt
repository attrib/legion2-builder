package builder.data

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
