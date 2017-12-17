package builder.ui

import builder.*
import builder.data.Unit
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import react.RBuilder
import react.dom.div
import react.dom.img
import react.setState

interface SelectedUnitInfoEventHandler {
    fun deselect()
    fun recall()
    fun undeploy()
    fun upgrade(upgradeTo: UnitDef)
}

fun RBuilder.selectedUnitInfo(selectedUnit: Unit?, build: Build, eventHandler: SelectedUnitInfoEventHandler) {
    div("selected-unit") {
        if (selectedUnit != null) {
            div("row no-gutters") {
                div("col-auto") {
                    attrs.title = "Unselect"
                    unitUi(selectedUnit.def, { eventHandler.deselect() })
                }
                if (selectedUnit.buildLevel == build.currentLevel) {
                    div("col-auto") {
                        img("Recall", "Icons/Recall.png") { attrs.title = "Recall" }
                        attrs.onClickFunction = {
                            eventHandler.recall()
                        }
                    }
                } else {
                    div("col-auto") {
                        img("Undeploy", "Icons/Undeploy.png") { attrs.title = "Undeploy" }
                        attrs.onClickFunction = {
                            eventHandler.undeploy()
                        }
                    }
                }
                LegionData.fighters(build.legion!!)
                        .filter { it.upgradesFrom == selectedUnit.def.id }
                        .forEach {
                            div("col-auto") {
                                attrs.title = "Upgrade"
                                unitUi(it, { eventHandler.upgrade(it) })
                            }
                        }
            }
        } else {
            div {
                +"Select unit"
            }
        }
    }

}