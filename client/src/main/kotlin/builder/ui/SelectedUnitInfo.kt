package builder.ui

import builder.data.UnitSelection
import builder.fighters
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import ltd2.Build
import ltd2.LegionData
import ltd2.UnitDef
import react.RBuilder
import react.dom.div

interface SelectedUnitInfoEventHandler {
    fun deselect()
    fun recall()
    fun undeploy()
    fun upgrade(upgradeTo: UnitDef)
    fun downgrade()
}

fun RBuilder.selectedUnitInfo(selectedUnit: UnitSelection, build: Build, eventHandler: SelectedUnitInfoEventHandler) {
    div("selected-unit") {
        if (selectedUnit.isBuiltUnit()) {
            div("row no-gutters") {
                div("col-auto") {
                    attrs.title = "Unselect"
                    unitUi(selectedUnit.getBuiltUnit().def, { eventHandler.deselect() })
                }
                if (selectedUnit.getBuiltUnit().buildLevel == build.currentLevel) {
                    div("col-auto") {
                        IconImg( "Icons/Recall.png", "Recall")
                        attrs.onClickFunction = {
                            eventHandler.recall()
                        }
                    }
                    if (selectedUnit.getBuiltUnit().upgradedFrom !== null) {
                        div("col-auto") {
                            IconImg( selectedUnit.getBuiltUnit().upgradedFrom!!.def.iconPath, "Downgrade")
                            attrs.onClickFunction = {
                                eventHandler.downgrade()
                            }
                        }
                    }
                } else {
                    div("col-auto") {
                        IconImg( "Icons/Undeploy.png", "Undeploy")
                        attrs.onClickFunction = {
                            eventHandler.undeploy()
                        }
                    }
                }
                LegionData.fighters(build.legion)
                        .filter { it.upgradesFrom == selectedUnit.getBuiltUnit().def.id }
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