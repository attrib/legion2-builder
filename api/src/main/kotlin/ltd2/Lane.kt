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
        return Lane(list)
    }

    override fun save(output: KOutput, obj: Lane) {
        obj.apply {
            output.writeShortValue(list.size.toShort())
            list.forEach { output.write(UnitSerializer, it) }
        }
    }

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("ltd2.Lane")
}
class Lane(val list : MutableList<UnitState> = mutableListOf()) {
    var units = Units(list)

    fun getWorkerCount(level: Int):Int {
        return 1 + getFighters(level).worker().size
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
        return costs
    }

    fun getValue(level: Int): Int {
        return getFighters(level).fighters().totalValue()
    }

    fun getFoodCosts(level: Int): Int {
        return getFighters(level).totalFood()
    }

    fun getIncome(level: Int): Int {
        return getFighters(level).totalIncome()
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

}