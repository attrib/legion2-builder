package builder



class Game {
    val data = LegionData

    fun fighters() = data.units.filter { it.unitClass == UnitClass.Fighter }
    fun fighters(legion: Legion) = fighters().filter { it.legion == legion.id }
    fun mercenaries() = data.units.filter { it.unitClass == UnitClass.Mercenary }

    fun getWaveCreaturesDef(level: Int): List<UnitDef> {
        val creatureDefs = mutableListOf<UnitDef>()
        val wave = data.waves[level]
        (0 until wave.amount).forEach { creatureDefs.add(data.unitsMap[wave.unit]!!) }
        if (wave.amount2 > 0) {
            (0 until wave.amount2).forEach { creatureDefs.add(data.unitsMap[wave.unit2]!!) }
        }
        return creatureDefs
    }
}