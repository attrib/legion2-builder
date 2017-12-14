package builder.data

import builder.ArmorType
import builder.AttackType
import builder.GlobalDef

class Global(val def: GlobalDef) {

    fun getModifier(attackType: AttackType, armorType: ArmorType): Double {
        return when (attackType) {
            AttackType.Pierce -> def.attackPierce[armorType.ordinal]
            AttackType.Impact -> def.attackNormal[armorType.ordinal]
            AttackType.Magic -> def.attackMagic[armorType.ordinal]
            AttackType.Siege -> def.attackSiege[armorType.ordinal]
            AttackType.Pure -> def.attackChaos[armorType.ordinal]
            else -> 1.0
        }
    }
}