package builder.data

import kotlin.js.Json

class Unit(val json: Json) {
        val name: String by jp(json)
        val legion: List<String> by jp(json)
        val description: String by jp(json)
        val iconpath: String by jp(json)
        val hp: Int by jpInt(json)
        val dps: Double by jpDouble(json)
        val attackType: AttackType? by jp(json, { s -> AttackType.values().find { it.name == s as String } })
        val armorType: DefenseType? by jp(json, { s -> DefenseType.values().find { it.name == s as String } })
        val attackMode: String? by jp(json)
        val range: Int by jpInt(json)
        val unitClass: UnitClass? by jp(json, { UnitClass.valueOf(it as String) })
        val upgradesTo: List<String> by jp(json)
        val upgradesFrom: List<String> by jp(json)
        val goldcost: Int by jpInt(json)
        val foodcost: Int by jpInt(json)
        val totalvalue: Int? by jpInt(json)
        val totalfood: Int? by jpInt(json)
        val goldvalue: Int? by jpInt(json)
        val mythiumcost: Int? by jpInt(json)
        val incomebonus: Int? by jpInt(json)
        val isenabled: String by jp(json)
        val legion_id: String by jp(json)

        val isEnabled: Boolean get() = isenabled == "True" && !name.startsWith("Test")
        var buildLevel: Int? = null
        var amount = 1


        fun copy(): Unit {
                val cloned = Unit(json)
                cloned.buildLevel = buildLevel
                cloned.amount = amount
                return cloned
        }
}
