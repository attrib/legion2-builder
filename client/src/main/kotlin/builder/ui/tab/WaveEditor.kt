package builder.ui.tab

import builder.buildableFighters
import builder.data.UnitSelection
import builder.getWaveCreaturesDef
import builder.mercenaries
import builder.ui.*
import kotlinx.html.id
import ltd2.*
import react.RBuilder
import react.dom.aside
import react.dom.div
import react.dom.h2
import react.dom.hr

interface WaveEditorEventHandler : BuildAreaEventHandler, SelectedUnitInfoEventHandler {
    fun selectNewFighter(unitDef: UnitDef)
    fun addMercenary(unitDef: UnitDef)
    fun removeMercenary(unit: UnitState)
    fun addResearch(researchDef: ResearchDef)
    fun removeResearch(research: Research)
}

fun RBuilder.waveEditor(build: Build, selectedUnit: UnitSelection, eventHandler: WaveEditorEventHandler) {
    div("row") {
        div("col-8") {
            div {
                attrs.id = "wave-creatures"
                unitList(LegionData.getWaveCreaturesDef(build.currentLevel), {}, selectedUnit)
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
                    unitList(LegionData.buildableFighters(build.legion!!), { unit ->
                        eventHandler.selectNewFighter(unit as UnitDef)
                    }, selectedUnit)
                }
            }
            div {
                h2 { +"Research" }
                div {
                    div {
                        unitList(build.getResearches(), { eventHandler.removeResearch(it as Research) }, selectedUnit, build.getAllResearches())
                    }
                    div {
                        unitList(LegionData.researches, { eventHandler.addResearch(it as ResearchDef) }, selectedUnit, build.getAllResearches())
                    }
                }
            }
            div {
                h2 { +"Mercenaries" }
                div {
                    div {
                        unitList(build.getMerchenaries(), { eventHandler.removeMercenary(it as UnitState) }, selectedUnit)
                    }
                    div {
                        unitList(LegionData.mercenaries(), { eventHandler.addMercenary(it as UnitDef) }, selectedUnit)
                    }
                }
            }

        }
    }

}