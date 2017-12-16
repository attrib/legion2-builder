package builder.data

import builder.UnitDef

class Unit(val def: UnitDef) {
    //        val isEnabled: Boolean get() = !def.id.startsWith("test")
        var buildLevel: Int? = null
}
