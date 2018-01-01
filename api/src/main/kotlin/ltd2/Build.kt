package ltd2

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl

object BuildSerializer : KSerializer<Build> {
    override fun load(input: KInput): Build {
        var legionId : String? = input.readStringValue()
        if( legionId=="") legionId = null
        val legion = if( legionId!=null ) {
            LegionData.legionsMap[legionId]
        } else {
            null
        }
        val currentLevel = input.readIntValue()
        val lane = input.read(LaneSerializer)
        val build = Build(lane)
        build.legion = legion
        build.legionId = legionId
        build.currentLevel = currentLevel
        return build
    }

    override fun save(output: KOutput, obj: Build) {
        output.writeStringValue(obj.legion?.id ?: "")
        output.writeIntValue(obj.currentLevel)
        output.write(LaneSerializer, obj.lane)
    }

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("ltd2.Build")
}

class Build(val lane: Lane = Lane()) {
    var legion: Legion? = null
    var legionId: String? = null

    var currentLevel = 0

    val costs get() = lane.getCosts(currentLevel)
    val available get() = reward() - costs
    val foodCosts get() = lane.getFoodCosts(currentLevel)
    val maxFood get() = lane.getMaxFood(currentLevel)
    val income get() = lane.getIncome(currentLevel)
    val totalHp get() = lane.getTotalHp(currentLevel)
    val totalDps get() = lane.getTotalDps(currentLevel)

    private fun reward(): Int {
        var reward = 250
        if (currentLevel > 0) {
            for (i in 0 until (currentLevel)) {
                reward += LegionData.waves[i].totalReward
                reward += lane.getIncome(i)
            }
        }
        return reward
    }

    fun levelIncrease() {
        if (currentLevel < LegionData.waves.size - 1) {
            currentLevel++
        }
    }

    fun levelDecrease() {
        if (currentLevel > 0) {
            currentLevel--
        }
    }

    fun getWorkerCount(): Int {
        return lane.getWorkerCount(currentLevel)
    }

    fun getFighters(): Units {
        return lane.getFighters(currentLevel)
    }

    fun getFightersUnfiltered(): Units {
        return lane.getFightersUnfiltered()
    }

    fun addFighter(unit: UnitDef, position: Position): UnitState {
        return lane.addFighter(unit, currentLevel, position)
    }

    fun removeFighter(unit: UnitState) {
        lane.removeFighter(unit)
    }

    fun upgradeFighter(selectedUnit: UnitState, upgradeUnitDef: UnitDef): UnitState {
        return lane.upgradeFighter(selectedUnit, upgradeUnitDef, currentLevel)
    }

    fun sellFighter(selectedUnit: UnitState) {
        lane.sellFighter(selectedUnit, currentLevel)
    }

    fun getResearches(): List<Research> {
        return lane.getResearchesFromLevel(currentLevel)
    }

    fun getAllResearches(): List<Research> {
        return lane.getResearches(currentLevel)
    }

    fun addResearch(researchDef: ResearchDef) {
        return lane.addResearch(researchDef, currentLevel)
    }

    fun removeResearch(research: Research) {
        lane.removeResearch(research)
    }

    fun getMerchenaries(): Units {
        return lane.getMerchenaries(currentLevel)
    }

    fun getMerchenaries(level: Int): Units {
        return lane.getMerchenaries(level)
    }

    fun addMerchenary(unit: UnitDef) {
        lane.addMerchenary(unit, currentLevel)
    }

    fun removeMerchenary(unit: UnitState) {
        lane.removeMerchenary(unit)
    }

    fun getResistance(testUnit: UnitDef?): Resistance {
        return Resistance(lane.getFighterDef(currentLevel), testUnit)
    }

    fun getCostsByLevel(level: Int): Int {
        return lane.getCosts(level)
    }

    fun getValueByLevel(level: Int): Int {
        return lane.getValue(level)
    }
}