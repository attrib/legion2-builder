package builder.ui

import builder.Build
import builder.data.Unit
import kotlinx.html.id
import react.RBuilder
import react.dom.div

interface BuildAreaEventHandler {
    fun selectUnit(unit: Unit)
}

fun RBuilder.buildArea(build: Build, selectedUnit: Unit?, eventHandler: BuildAreaEventHandler) {
    div {
        attrs.id = "build-area"
        unitList(build.getFighters(), { true }, { eventHandler.selectUnit(it) }, selectedUnit)
    }

}