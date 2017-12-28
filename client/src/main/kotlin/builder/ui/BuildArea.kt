package builder.ui

import builder.data.UnitSelection
import d3.d3_wrapper
import ltd2.Build
import ltd2.UnitState
import kotlinx.html.id
import ltd2.UnitDef
import react.RBuilder
import react.dom.div

interface BuildAreaEventHandler {
    fun selectUnit(unit: UnitState)
    fun addFighter(unit: UnitDef, x: Int, y: Int)
}

fun RBuilder.buildArea(build: Build, selectedUnit: UnitSelection, eventHandler: BuildAreaEventHandler) {
    div {
        attrs.id = "build-area"
        d3_wrapper.render("build-area", selectedUnit, build.getFighters().removeWorkersNotLevel(build.currentLevel).toTypedArray(),
                { unitDef: UnitDef, x: Int, y: Int ->
                    eventHandler.addFighter(unitDef, x, y)
                },
                { unitState: UnitState ->
                    eventHandler.selectUnit(unitState)
                })
    }
}