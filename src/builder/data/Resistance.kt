package builder.data

class Resistance(units: List<Unit>) {

    val dps: MutableMap<AttackType, Double> = mutableMapOf()
    val hps: MutableMap<DefenseType, Int> = mutableMapOf()

    init {
        for (unit in units) {
            if (!dps.containsKey(unit.attackType!!)) {
                dps.put(unit.attackType!!, 0.0)
            }
            val oldDps = dps[unit.attackType!!]
            dps[unit.attackType!!] = unit.dps + oldDps!!
            if (!hps.containsKey(unit.armorType!!)) {
                hps.put(unit.armorType!!, 0)
            }
            val oldHp = hps[unit.armorType!!]
            hps[unit.armorType!!] = unit.hp + oldHp!!
        }
    }
}