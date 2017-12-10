package builder.data

class Unit(
        val name: String,
        val legion: List<String>,
        val description: String,
        val iconpath: String,
        val hp: Int,
        val dps: Double?,
        val attackType: AttackType,
        val armorType: DefenseType,
        val attackMode: String,
        val range: Int?,
        val unitClass: UnitClass,
        val upgradesTo: List<String>,
        val upgradesFrom: List<String>,
        val goldcost: Int?,
        val foodcost: Int?,
        val totalvalue: Int?,
        val totalfood: Int?,
        val goldvalue: Int?,
        val mythiumcost: Int?,
        val incomebonus: Int?,
        val isenabled: String,
        val legion_id: String
        )
{

        val isEnabled: Boolean get() = isenabled == "True"

}

/*

                            unitJson["hp"] as Int,
                            unitJson["dps"] as Float,
                            attackType,
                            armorType,
                            unitJson["attackmode"] as String,
                            unitJson["range"] as Int,
                            unitClass,
                            mutableListOf(),
                            mutableListOf(),
                            unitJson["goldcost"] as Int,
                            unitJson["foodcost"] as Int,
                            unitJson["totalvalue"] as Int,
                            unitJson["totalfood"] as Int,
                            unitJson["goldvalue"] as Int,
                            unitJson["mythiumcost"] as Int,
                            unitJson["incomebonus"] as Int,


 */