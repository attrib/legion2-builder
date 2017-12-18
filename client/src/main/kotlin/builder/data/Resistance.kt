package builder.data

import ltd2.ArmorType
import ltd2.AttackType
import ltd2.LegionData
import ltd2.UnitDef

class Resistance(units: List<UnitDef>, val testUnit: UnitDef?) {

    val dps: MutableMap<AttackType, Double> = mutableMapOf()
    val hps: MutableMap<ArmorType, Int> = mutableMapOf()

    init {
        for (unit in units) {
            if (!dps.containsKey(unit.attackType)) {
                dps.put(unit.attackType, 0.0)
            }
            val oldDps = dps[unit.attackType]
            dps[unit.attackType] = unit.dmgBase * unit.attackSpeed + oldDps!!
            if (!hps.containsKey(unit.armorType)) {
                hps.put(unit.armorType, 0)
            }
            val oldHp = hps[unit.armorType]
            hps[unit.armorType] = unit.hitpoints + oldHp!!
        }
    }

    fun getModAttack(attackType: AttackType): Double {
        if (testUnit?.armorType !== null) {
            return LegionData.global.getModifier(attackType, testUnit.armorType)
        }
        return 0.0
    }

    fun getModDefense(defenseType: ArmorType): Double {
        if (testUnit?.attackType !== null) {
            return LegionData.global.getModifier(testUnit.attackType, defenseType)
        }
        return 0.0
    }
}