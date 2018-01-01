package ltd2

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerialClassDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlin.math.roundToInt


object LaneSerializer : KSerializer<Lane> {
    override fun load(input: KInput): Lane {
        val len = input.readShortValue()
        val list = mutableListOf<UnitState>()
        (0 until len).forEach {
            list += input.read(UnitSerializer)
        }
        val lenResearch = input.readShortValue()
        val researchList = mutableListOf<Research>()
        (0 until lenResearch).forEach {
            researchList += input.read(ResearchSerializer)
        }
        return Lane(list, researchList)
    }

    override fun save(output: KOutput, obj: Lane) {
        obj.apply {
            output.writeShortValue(list.size.toShort())
            list.forEach { output.write(UnitSerializer, it) }
            output.writeShortValue(researches.size.toShort())
            researches.forEach { output.write(ResearchSerializer, it) }
        }
    }

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("ltd2.Lane")
}
class Lane(val list : MutableList<UnitState> = mutableListOf(), val researches: MutableList<Research> = mutableListOf()) {
    var units = Units(list)

    fun getWorkerCount(level: Int):Int {
        return 1 + researches.filter { it.def.id == Research.WORKER_ID }.sumBy { if (it.buildLevel!! <= level) 1 else 0 }
    }

    fun getTotalHp(level: Int): Int {
        return getFighters(level).fighters().totalHp()
    }

    fun getTotalDps(level: Int): Double {
        return getFighters(level).fighters().totalDps()
    }

    fun getCosts(level: Int): Int {
        var costs = getFighters(level).fighters().totalValue()
        costs += getSoldFighter(level).sumByDouble { it.def.totalValue * 0.6 }.roundToInt()
        costs += getResearches(level).sumBy { it.def.goldCost + it.def.goldCostPerLevel * it.upgradeLevel }
        return costs
    }

    fun getValue(level: Int): Int {
        return getFighters(level).fighters().totalValue()
    }

    fun getFoodCosts(level: Int): Int {
        return getFighters(level).totalFood()
    }

    fun getMaxFood(level: Int): Int {
        return 15 + researches.filter { it.def.id == Research.SUPPLY_RESEARCH_ID }.sumBy { if (it.buildLevel!! <= level) it.def.bonussupply else 0 }
    }

    fun getIncome(level: Int): Int {
        return units.mercenaries().upToLevel(level).totalIncome()
    }

    fun getFighters(level: Int): Units {
        return units
                .ownCreatures()
                .upToLevel(level)
                .notSold(level)
                .notUpgraded(level)
    }

    fun getFightersUnfiltered(): Units {
        return units
    }

    fun getFighterDef(level: Int, includeWorkers: Boolean = false): List<UnitDef> {
        if (includeWorkers) {
            return getFighters(level).map { it.def }
        } else {
            return getFighters(level).fighters().map { it.def }
        }
    }

    fun upgradeFighter(selectedUnit: UnitState, upgradeTo: UnitDef, level: Int): UnitState {
        selectedUnit.upgradedLevel = level
        return addFighter(upgradeTo, level, selectedUnit.position)
    }

    fun sellFighter(selectedUnit: UnitState, level: Int) {
        selectedUnit.soldLevel = level
    }

    fun getSoldFighter(level: Int): Units {
        return units.sold(level)
    }

    fun addFighter(unit: UnitDef, level: Int, position: Position): UnitState {
        val newUnit = UnitState(unit)
        newUnit.buildLevel = level
        newUnit.position = position
        list.add(newUnit)
        units = Units(list)
        return newUnit
    }

    fun removeFighter(unit: UnitState) {
        list.remove(unit)
        units = Units(list)
    }

    fun getMerchenaries(level: Int): Units {
        return units.mercenaries().forLevel(level)
    }

    fun addMerchenary(unit: UnitDef, level: Int) {
        val newUnit = UnitState(unit)
        newUnit.buildLevel = level
        list.add(newUnit)
        units = Units(list)
    }

    fun removeMerchenary(unit: UnitState) {
        list.remove(unit)
        units = Units(list)
    }


    fun getResearchesFromLevel(level: Int):List<Research> {
        return researches.filter { it.buildLevel == level }
    }

    fun getResearches(level: Int): List<Research> {
        return researches.filter { it.buildLevel!! <= level }
    }

    fun addResearch(researchDef: ResearchDef, level: Int) {
        val newResearch = Research(researchDef)
        newResearch.buildLevel = level
        researches.add(newResearch)
        resetUpgradeLevels()
    }

    fun removeResearch(research: Research) {
        researches.remove(research)
        resetUpgradeLevels()
    }

    private fun resetUpgradeLevels() {
        // reset upgrade levels as it can be changed if a upgrade except the last was removed
        val upgradeLevels = mutableMapOf<ResearchDef, Int>()
        researches.sortBy { it.buildLevel }
        researches.forEach {
            if (upgradeLevels.contains(it.def)) {
                it.upgradeLevel = upgradeLevels[it.def]!!
            }
            else {
                it.upgradeLevel = 0
            }
            upgradeLevels[it.def] = it.upgradeLevel + 1
        }
    }
}