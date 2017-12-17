package builder.ui

import builder.Build
import builder.data.Unit
import kotlinx.html.id
import react.RBuilder
import react.dom.div
import react.dom.li
import react.dom.ul

interface BuildAreaEventHandler {
    fun selectUnit(unit: Unit)
}

fun RBuilder.buildArea(build: Build, selectedUnit: Unit?, eventHandler: BuildAreaEventHandler) {
    div {
        attrs.id = "build-area"
        if (build.getFighters(true).isNotEmpty()) {
            ul("list-inline row no-gutters justify-content-md-center") {
                build.getFighters(true).forEach { unit ->
                    li("col-auto") {
                        val addClass = if (selectedUnit == unit) "selected" else ""
                        unitUi(unit.def, {
                            eventHandler.selectUnit(unit)
                        }, addClass)
                    }
                }
            }
        }
    }

}