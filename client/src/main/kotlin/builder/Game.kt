package builder

import ltd2.*


fun LegionData.fighters() = units.filter { it.unitClass == UnitClass.Fighter && it.isEnabled() }
fun LegionData.fighters(legion: Legion?) = if (legion != null) fighters().filter { it.legion == legion.id } else emptyList()
fun LegionData.buildableFighters(legion: Legion?) = if (legion != null) fighters(legion).filter { it.upgradesFrom == null && it.goldCost > 0 } else emptyList()
fun LegionData.mercenaries() = units.filter { it.unitClass == UnitClass.Mercenary && it.isEnabled() }
fun LegionData.getWaveCreaturesDef(level: Int): List<UnitDef> {
    val creatureDefs = mutableListOf<UnitDef>()
    val wave = waves[level]
    (0 until wave.amount).forEach { creatureDefs.add(unitsMap[wave.unit]!!) }
    if (wave.amount2 > 0) {
        (0 until wave.amount2).forEach { creatureDefs.add(unitsMap[wave.unit2]!!) }
    }
    return creatureDefs
}
