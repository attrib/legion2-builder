package app

import builder.*
import builder.data.Resistance
import builder.data.Unit
import builder.data.isEnabled
import builder.ui.dpsUi
import builder.ui.hpUi
import builder.ui.unitUi
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get
import org.w3c.files.Blob
import org.w3c.files.FileReader
import org.w3c.files.get
import parser.LogParser
import parser.ReplayResult
import parser.replay
import react.*
import react.dom.*

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

interface AppState : RState {
    var build: Build
    var selectedUnit: Unit?
    var selectedPlayer: String?
    var replayResult: ReplayResult
    var uploadingFile: Boolean
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        resetBuild()
        uploadingFile = false
    }

    fun AppState.resetBuild() {
        build = Build()
        build.legion = LegionData.legionsMap["element_legion_id"]!!
        build.legionId = "element_legion_id"
        selectedUnit = null
    }

    override fun RBuilder.render() {
        nav("navbar navbar-expand-md navbar-dark fixed-top bg-dark") {
            a(href = "#", classes = "navbar-brand") {
                +"Legion TD 2 Builder"
            }
            ul("mr-auto navbar-nav") {
                li("nav-item") {
                    a(href = "https://legiontd2.com", classes = "nav-link", target = "_blank") {
                        +"Official LTD2"
                    }
                }
            }
            a(href = "https://github.com/attrib/legion2-builder", classes = "btn btn-outline-success my-2 my-sm-0") {
                +"Github"
            }
        }

        if (state.uploadingFile) {
            div("loading") { +"Loading" }
        } else {
            div("header") {
                div("container") {
                    div("row") {
                        div("col") {
                            h3 { +"Select Legion" }
                            div("btn-group btn-group-sm") {
                                LegionData.legionsMap.forEach { (legionId, legion) ->
                                    if (!legion.playable) {
                                        return@forEach
                                    }
                                    label("btn btn-secondary btn-sm col") {
                                        if (state.build.legionId == legionId) {
                                            attrs.classes += "active"
                                        }
                                        input(type = InputType.radio, name = "legion", classes = "d-none") {
                                            attrs.onChangeFunction = {
                                                val target = it.target as HTMLInputElement
                                                setState {
                                                    build.legionId = target.value
                                                    build.legion = LegionData.legionsMap[target.value]
                                                }
                                            }
                                            attrs.value = legionId
                                            if (state.build.legionId == legionId) {
                                                attrs.checked = true
                                            }
                                        }
                                        img(alt = legion.name, src = legion.iconPath) {
                                            attrs.title = legion.name
                                            attrs.width = "32px"
                                        }
                                    }
                                }
                            }
                            div("btn-group") {
                                button(classes = "btn btn-secondary col") {
                                    +"reset"
                                    attrs.onClickFunction = {
                                        setState {
                                            resetBuild()
                                        }
                                    }
                                }
                                a(href = "", classes = "btn btn-secondary col") {
                                    +"Permalink"
                                }
                            }
                            div("btn-group") {
                                input(InputType.file, classes = "btn btn-secondary col") {
                                    attrs.onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        val fr = FileReader()
                                        fr.onload = {
                                            val text = (it.target!! as FileReader).result as String
                                            setState {
                                                replayResult = replay(LogParser(text).parse())
                                                uploadingFile = false
                                            }
                                        }
                                        fr.readAsText(target.files!![0] as Blob)
                                        setState {
                                            uploadingFile = true
                                        }
                                    }
                                }
                                select(classes = "btn btn-secondary col") {
                                    state.replayResult?.playerBuilds?.keys?.forEach { player ->
                                        option {
                                            attrs.selected = player == state.selectedPlayer ?: ""
                                            +player
                                        }
                                    }
                                    attrs.onChangeFunction = {
                                        val target = it.target as HTMLSelectElement
                                        val player = target.selectedOptions[0]
                                        setState {
                                            selectedPlayer = player?.innerHTML
                                            build = state.replayResult?.playerBuilds?.get(selectedPlayer!!)!!
                                        }
                                    }
                                }

                            }
                        }
                        div("col") {
                            h3 { +"Lane info" }
                            val unitId = LegionData.waves[state.build.currentLevel].unit
                            val unitDef = LegionData.unitsMap[unitId]
                            val waveDef = LegionData.getWaveCreaturesDef(state.build.currentLevel)

                            div("tooltip-parent") {
                                +"Total HP: ${state.build.totalHp}"
                                hpUi(state.build.getResistance(unitDef))
                            }
                            div("tooltip-parent") {
                                +"Total DPS: ${state.build.totalDps.format(2)}"
                                dpsUi(state.build.getResistance(unitDef))
                            }
//                        div {
//                            +"Survivability Chance: ${state.build.survivability(waveDef)}"
//                        }
                            div {
                                +"Workers: ${state.build.getWorkerCount()}"
                            }
                        }
                        div("col") {
                            h3 { +"Build info" }
                            div {
                                +"Cost: "
                                +state.build.costs.toString()
                                +" / "
                                +LegionData.waves[state.build.currentLevel].recommendedValue.toString()
                            }
                            div {
                                +"Food: "
                                +state.build.foodCosts.toString()
//                            +" / "
//                            +"15"
                            }
                            div {
                                +"Available: "
                                +state.build.available.toString()
                            }
                            div {
                                +"Income: "
                                +state.build.income.toString()
                            }
                        }
                        div("col") {
                            val waveDef = LegionData.getWaveCreaturesDef(state.build.currentLevel)
                            h3 { +"Wave Info" }
                            div {
                                +"Level: "
                                +(state.build.currentLevel + 1).toString()
                            }
                            div("tooltip-parent") {
                                +"Total HP: "
                                +waveDef.sumBy { it.hitpoints }.toString()
                                hpUi(Resistance(waveDef, null))
                            }
                            div("tooltip-parent") {
                                +"Total DPS: "
                                +waveDef.sumByDouble { it.dmgBase * it.attackSpeed }.format(2)
                                dpsUi(Resistance(waveDef, null))
                            }
                            div("btn-group btn-group-sm") {
                                button(classes = "btn btn-secondary btn-sm col") {
                                    +"-"
                                    attrs.onClickFunction = {
                                        setState { state.build.levelDecrease() }
                                    }
                                }
                                button(classes = "btn btn-secondary btn-sm col") {
                                    +"+"
                                    attrs.onClickFunction = {
                                        setState { state.build.levelIncrease() }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            div("container") {
                div("row") {
                    div("col-8") {
                        attrs.id = "build-area"
                        if (state.build.getFighters(true).isNotEmpty()) {
                            ul("list-inline row no-gutters justify-content-md-center") {
                                state.build.getFighters(true).forEach { unit ->
                                    li("col") {
                                        unitUi(unit.def, { setState { selectedUnit = unit } })
                                    }
                                }
                            }
                        }
                    }
                    aside("col-4") {

                        div("selected-unit") {
                            val selectedUnit = state.selectedUnit
                            if (selectedUnit != null) {
                                div("row no-gutters") {
                                    div("col-auto") {
                                        attrs.title = "Unselect"
                                        unitUi(selectedUnit.def, { setState { this.selectedUnit = null } })
                                    }
                                    if (selectedUnit.buildLevel == state.build.currentLevel) {
                                        div("col-auto") {
                                            img("Recall", "Icons/Recall.png") { attrs.title = "Recall" }
                                            attrs.onClickFunction = {
                                                setState {
                                                    build.removeFighter(selectedUnit)
                                                    this.selectedUnit = null
                                                }
                                            }
                                        }
                                    } else {
                                        div("col-auto") {
                                            img("Undeploy", "Icons/Undeploy.png") { attrs.title = "Undeploy" }
                                            attrs.onClickFunction = {
                                                setState {
                                                    build.sellFighter(selectedUnit)
                                                    this.selectedUnit = null
                                                }
                                            }
                                        }
                                    }
                                    LegionData.fighters(state.build.legion!!)
                                            .filter { it.upgradesFrom == selectedUnit.def.id }
                                            .forEach {
                                                div("col-auto") {
                                                    attrs.title = "Upgrade"
                                                    unitUi(it, { setState { this.selectedUnit = build.upgradeFighter(selectedUnit, it) } })
                                                }
                                            }
                                }
                            } else {
                                div {
                                    +"Select unit"
                                }
                            }
                        }

                        div {
                            if (state.build.legion == null) {
                                +"Please select legion"
                            } else {
                                ul("list-inline row no-gutters justify-content-start") {
                                    (LegionData.fighters(state.build.legion!!) + LegionData.upgrades()).forEach { unit ->
                                        if (unit.isEnabled() && unit.upgradesFrom == null) {
                                            li("col-auto") {
                                                unitUi(unit, { setState { selectedUnit = build.addFighter(unit) } })
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        div {
                            h2 { +"Mercenaries" }
                            div {
                                div {
                                    if (state.build.getMerchenaries().isNotEmpty()) {
                                        ul("list-inline row no-gutters justify-content-start") {
                                            state.build.getMerchenaries().forEach { unit ->
                                                li("col-auto") {
                                                    unitUi(unit.def, { setState { build.removeMerchenary(unit) } })
                                                }
                                            }
                                        }
                                    }
                                }
                                div {
                                    ul("list-inline row no-gutters justify-content-start") {
                                        LegionData.mercenaries().forEach { unit ->
                                            if (unit.isEnabled()) {
                                                li("col-auto") {
                                                    unitUi(unit, { setState { build.addMerchenary(unit) } })
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        footer("container footer") {
            +"Images and data are property of AutoAttack Games, Inc."
            br { }
            +"Legion TD, and Legion TD 2 are registered trademarks of AutoAttack Games, Inc."
        }
    }
}

fun RBuilder.app() = child(App::class) {}