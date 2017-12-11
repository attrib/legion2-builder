package builder.data

import kotlin.js.Json

class Global(json: Json) {
    val name: String by jp(json)
    val attackchartpierce: List<Double> by jpDoubleList(json)
    val attackchartnormal: List<Double> by jpDoubleList(json)
    val attackchartmagic: List<Double> by jpDoubleList(json)
    val attackchartsiege: List<Double> by jpDoubleList(json)
    val attackchartchaos: List<Double> by jpDoubleList(json)

    fun getModifier(attackType: AttackType, armorType: DefenseType): Double {
        return when (attackType) {
            AttackType.Pierce -> attackchartpierce[armorType.ordinal]
            AttackType.Impact -> attackchartnormal[armorType.ordinal]
            AttackType.Magic -> attackchartmagic[armorType.ordinal]
            AttackType.Siege -> attackchartsiege[armorType.ordinal]
            AttackType.Pure -> attackchartchaos[armorType.ordinal]
        }
    }
}