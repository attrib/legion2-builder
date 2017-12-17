package builder.ui.tab

import builder.*
import builder.data.Unit
import builder.data.isEnabled
import builder.ui.*
import kotlinx.html.id
import react.RBuilder
import react.dom.aside
import react.dom.div
import react.dom.h2
import react.dom.hr

interface WaveEditorEventHandler : BuildAreaEventHandler, SelectedUnitInfoEventHandler {
    fun addFighter(unitDef: UnitDef)
    fun addMercenary(unitDef: UnitDef)
    fun removeMercenary(unit: Unit)
}

fun RBuilder.waveEditor(build: Build, selectedUnit: Unit?, eventHandler: WaveEditorEventHandler) {
    div("row") {
        div("col-8") {
            div {
                attrs.id = "wave-creatures"
                unitList(LegionData.getWaveCreaturesDef(build.currentLevel), { it.isEnabled() }, {})
            }
            hr { }
            buildArea(build, selectedUnit, eventHandler)
        }
        aside("col-4") {

            selectedUnitInfo(selectedUnit, build, eventHandler)

            div {
                if (build.legion == null) {
                    +"Please select legion"
                } else {
                    unitList(LegionData.fighters(build.legion!!) + LegionData.upgrades(), { unit ->
                        unit.isEnabled() && unit.upgradesFrom == null
                    }, { unit ->
                        eventHandler.addFighter(unit)
                    })
                }
            }

            div {
                h2 { +"Mercenaries" }
                div {
                    div {
                        unitList(build.getMerchenaries(), { true }, { eventHandler.removeMercenary(it) })
                    }
                    div {
                        unitList(LegionData.mercenaries(), { it.isEnabled() }, { eventHandler.addMercenary(it) })
                    }
                }
            }

        }
    }

}