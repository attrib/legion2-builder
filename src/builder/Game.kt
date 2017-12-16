package builder


fun LegionData.fighters() = units.filter { it.unitClass == UnitClass.Fighter }
fun LegionData.fighters(legion: Legion) = fighters().filter { it.legion == legion.id }
fun LegionData.upgrades() = units.filter { it.unitClass == UnitClass.Worker } //@todo: add supply upgrade here
fun LegionData.mercenaries() = units.filter { it.unitClass == UnitClass.Mercenary }
fun LegionData.getWaveCreaturesDef(level: Int): List<UnitDef> {
    val creatureDefs = mutableListOf<UnitDef>()
    val wave = waves[level]
    (0 until wave.amount).forEach { creatureDefs.add(unitsMap[wave.unit]!!) }
    if (wave.amount2 > 0) {
        (0 until wave.amount2).forEach { creatureDefs.add(unitsMap[wave.unit2]!!) }
    }
    return creatureDefs
}
