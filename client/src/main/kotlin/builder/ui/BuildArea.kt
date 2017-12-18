package builder.ui

import ltd2.Build
import ltd2.UnitState
import kotlinx.html.id
import react.RBuilder
import react.dom.div

interface BuildAreaEventHandler {
    fun selectUnit(unit: UnitState)
}

fun RBuilder.buildArea(build: Build, selectedUnit: UnitState?, eventHandler: BuildAreaEventHandler) {
    div {
        attrs.id = "build-area"
        unitList(build.getFighters().removeWorkersNotLevel(build.currentLevel), { true }, { eventHandler.selectUnit(it) }, selectedUnit)
    }

}