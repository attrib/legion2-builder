package builder.data

import ltd2.ArmorType
import ltd2.AttackType
import ltd2.Global

fun Global.getModifier(attackType: AttackType, armorType: ArmorType): Double {
    return when (attackType) {
        AttackType.Pierce -> attackPierce[armorType.ordinal]
        AttackType.Impact -> attackNormal[armorType.ordinal]
        AttackType.Magic -> attackMagic[armorType.ordinal]
        AttackType.Siege -> attackSiege[armorType.ordinal]
        AttackType.Pure -> attackChaos[armorType.ordinal]
        else -> 1.0
    }
}