package builder.data

import builder.UnitDef

class Legion(val name: String, val iconpath: String, val playable: Boolean) {
    val fighters: MutableMap<String, UnitDef> = mutableMapOf()
}