package builder.data

class Unit(
        val name: String,
        val legion: Array<String>,
        val description: String,
        val iconpath: String,
        val hp: Int,
        val dps: Float,
        val attackType: AttackType,
        val armorType: DefenseType,
        val attackMode: String,
        val range: Int,
        val unitClass: UnitClass,
        val upgradesTo: Array<String>,
        val upgradesFrom: Array<String>,
        val goldcost: Int,
        val foodcost: Int,
        val totalvalue: Int,
        val totalfood: Int,
        val goldvalue: Int,
        val mythiumcost: Int,
        val incomebonus: Int,
        val isenabled: String,
        val legion_id: String
        )