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
    val waveDef = LegionData.getWaveCreaturesDef(build.currentLevel)

    div("tooltip-parent") {
        +"Total HP: ${build.totalHp}"
        hpUi(build.getResistance(unitDef))
    }
    div("tooltip-parent") {
        +"Total DPS: ${build.totalDps.format(2)}"
        dpsUi(build.getResistance(unitDef))
    }
//                        div {
//                            +"Survivability Chance: ${state.build.survivability(waveDef)}"
//                        }
    div {
        +"Workers: ${build.getWorkerCount()}"
    }

}