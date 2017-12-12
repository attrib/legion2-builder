package builder.data

class Resistance(units: List<Unit>, val global: Global, val testUnit: Unit?) {

    val dps: MutableMap<AttackType, Double> = mutableMapOf()
    val hps: MutableMap<DefenseType, Int> = mutableMapOf()

    init {
        for (unit in units) {
            if (!dps.containsKey(unit.attackType!!)) {
                dps.put(unit.attackType!!, 0.0)
            }
            val oldDps = dps[unit.attackType!!]
            dps[unit.attackType!!] = unit.dps * unit.amount + oldDps!!
            if (!hps.containsKey(unit.armorType!!)) {
                hps.put(unit.armorType!!, 0)
            }
            val oldHp = hps[unit.armorType!!]
            hps[unit.armorType!!] = unit.hp * unit.amount + oldHp!!
        }
    }

    fun getModAttack(attackType: AttackType): Double {
        if (testUnit?.armorType !== null) {
            return global.getModifier(attackType, testUnit.armorType!!)
        }
        return 0.0
    }

    fun getModDefense(defenseType: DefenseType): Double {
        if (testUnit?.attackType !== null) {
            return global.getModifier(testUnit.attackType!!, defenseType!!)
        }
        return 0.0
    }
}