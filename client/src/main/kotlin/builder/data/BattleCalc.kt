package builder.data

import ltd2.Global
import ltd2.UnitDef

data class Result(val unitsA: List<UnitState>, val unitsB: List<UnitState>) {
    fun hpA() = unitsA.sumByDouble { it.hitpoints }
    fun hpB() = unitsB.sumByDouble { it.hitpoints }
}

data class UnitState(val unit: UnitDef, var hitpoints: Double, var target: UnitState? = null) {
    fun dealDamage(global: Global) {
        target?.let {
            val mod = global.getModifier(unit.attackType, it.unit.armorType)
            val effectiveDmg = mod * unit.dmgBase / unit.attackSpeed
            println("${unit.id}($hitpoints) deals $effectiveDmg to ${it.unit.id}(${it.hitpoints})")
            it.hitpoints -= effectiveDmg
            if (it.hitpoints <= 0) {
                target = null
            }
        }
    }
}

class BattleCalc(val global: Global, val unitsA: List<UnitDef>, val unitsB: List<UnitDef>, val targetSelectionStrategy: (List<UnitState>) -> UnitState) {
    fun calc(): Result {
        var unitStatesA = unitsA.map { UnitState(it, it.hitpoints.toDouble()) }
        var unitStatesB = unitsB.map { UnitState(it, it.hitpoints.toDouble()) }

        while (unitStatesA.isNotEmpty() && unitStatesB.isNotEmpty()) {
            assignTargets(unitStatesA, unitStatesB)
            assignTargets(unitStatesB, unitStatesA)
            unitStatesA.forEach { it.dealDamage(global) }
            unitStatesB.forEach { it.dealDamage(global) }
            unitStatesA = unitStatesA.filter { it.hitpoints > 0 }
            unitStatesB = unitStatesB.filter { it.hitpoints > 0 }
        }
        println("Result:")
        println(unitStatesA.joinToString { "${it.unit.id} ${it.hitpoints}" })
        println(unitStatesB.joinToString { "${it.unit.id} ${it.hitpoints}" })
        return Result(unitStatesA, unitStatesB)
    }

    fun assignTargets(list: List<UnitState>, targets: List<UnitState>) {
        list.filter { it.target == null }.forEach {
            it.target = targetSelectionStrategy(targets)
        }
    }
}