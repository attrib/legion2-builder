package builder.ui.header

import app.format
import ltd2.Build
import ltd2.LegionData
import builder.getWaveCreaturesDef
import builder.ui.dpsUi
import builder.ui.hpUi
import react.RBuilder
import react.dom.div
import react.dom.h3

fun RBuilder.laneInfo(build: Build) {
    h3 { +"Lane info" }
    val unitId = LegionData.waves[build.currentLevel].unit
    val unitDef = LegionData.unitsMap[unitId]
    val resistance = build.getResistance(unitDef)
    val waveDef = LegionData.getWaveCreaturesDef(build.currentLevel)

    div("tooltip-parent") {
        +"Total HP: ${build.totalHp}"
        +" ("
        +resistance.hps.map {
            val mod = resistance.getModDefense(it.key)
            it.value * mod
        }.sum().format(2)
        +")"
        hpUi(resistance)
    }
    div("tooltip-parent") {
        +"Total DPS: ${build.totalDps.format(2)}"
        +" ("
        +resistance.dps.map {
            val mod = resistance.getModAttack(it.key)
            it.value * mod
        }.sum().format(2)
        +")"
        dpsUi(resistance)
    }
    div {
        +"Total costs:"
        +build.costs.toString()
    }
//                        div {
//                            +"Survivability Chance: ${state.build.survivability(waveDef)}"
//                        }
    div {
        +"Workers: ${build.getWorkerCount()}"
    }

}