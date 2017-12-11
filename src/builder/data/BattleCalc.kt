package builder.data

data class Result(val unitsA: List<UnitState>, val unitsB: List<UnitState>) {
    fun hpA() = unitsA.sumByDouble { it.hitpoints }
    fun hpB() = unitsB.sumByDouble { it.hitpoints }
}

data class UnitState(val unit: Unit, var hitpoints: Double, var target: UnitState? = null) {
    fun dealDamage(global: Global) {
        target?.let {
            val mod = global.getModifier(unit.attackType!!, it.unit.armorType!!)
            val effectiveDmg = mod * unit.dmgbase / unit.aspd
            println("${unit.name}($hitpoints) deals $effectiveDmg to ${it.unit.name}(${it.hitpoints})")
            it.hitpoints -= effectiveDmg
            if (it.hitpoints <= 0) {
                target = null
            }
        }
    }
}

class BattleCalc(val global: Global, val unitsA: List<Unit>, val unitsB: List<Unit>, val targetSelectionStrategy: (List<UnitState>) -> UnitState) {
    fun calc(): Result {
        var unitStatesA = unitsA.map { UnitState(it, it.hp.toDouble()) }
        var unitStatesB = unitsB.map { UnitState(it, it.hp.toDouble()) }

        while (unitStatesA.isNotEmpty() && unitStatesB.isNotEmpty()) {
            assignTargets(unitStatesA, unitStatesB)
            assignTargets(unitStatesB, unitStatesA)
            unitStatesA.forEach { it.dealDamage(global) }
            unitStatesB.forEach { it.dealDamage(global) }
            unitStatesA = unitStatesA.filter { it.hitpoints > 0 }
            unitStatesB = unitStatesB.filter { it.hitpoints > 0 }
        }
        println("Result:")
        println(unitStatesA.joinToString { "${it.unit.name} ${it.hitpoints}" })
        println(unitStatesB.joinToString { "${it.unit.name} ${it.hitpoints}" })
        return Result(unitStatesA, unitStatesB)
    }

    fun assignTargets(list: List<UnitState>, targets: List<UnitState>) {
        list.filter { it.target == null }.forEach {
            it.target = targetSelectionStrategy(targets)
        }
    }
}