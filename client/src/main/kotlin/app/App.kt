package app

import builder.PermaLinkV1JS
import builder.buildableFighters
import builder.data.UnitSelection
import builder.getWaveCreaturesDef
import builder.ui.header.*
import builder.ui.tab.BuildOrderEventHandler
import builder.ui.tab.WaveEditorEventHandler
import builder.ui.tab.buildOrder
import builder.ui.tab.waveEditor
import d3.d3_wrapper
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import kotlinx.html.onKeyUp
import ltd2.*
import org.w3c.dom.PopStateEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.get
import parser.ReplayResult
import react.*
import react.dom.*
import kotlin.browser.document
import kotlin.browser.window

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

enum class Tabs {
    BuildOrder,
    WaveEditor
}

interface AppState : RState {
    var build: Build
    var selectedUnit: UnitSelection
    var selectedPlayer: String?
    var replayResult: ReplayResult?
    var uploadingFile: Boolean
    var selectedTab: Tabs
    var errorMessage: String?
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        uploadingFile = false
        selectedTab = Tabs.WaveEditor
        selectedUnit = UnitSelection()
        replayResult = null
        val url = window.location.href
        if (url.contains("?b=")) {
            val code = url.split("?b=")[1]
            try {
                build = PermaLinkV1JS.fromPermaLinkCode(code)
            }
            catch(e: Exception) {
                resetBuild()
                errorMessage = "The permalink is invalid or outdated. " + e.message
            }
        }
        else {
            resetBuild()
        }
        window.onpopstate = { event: Event ->
            val state = (event as PopStateEvent).state
            if (state !== null) {
                setState {
                    try {
                        build = PermaLinkV1JS.fromPermaLinkCode(state.toString())
                        selectedUnit.clearSelection()
                    }
                    catch(e: Exception) {
                        resetBuild()
                        errorMessage = "The permalink is invalid or outdated. " + e.message
                    }
                }
            }
        }
        d3_wrapper.init()
    }

    fun AppState.resetBuild() {
        build = Build()
        build.legion = LegionData.legionsMap["element_legion_id"]!!
        build.legionId = "element_legion_id"
        selectedUnit.clearSelection()
        updateHistory()
    }

    fun AppState.updateHistory() {
        val permalink = PermaLinkV1JS.toPermaLinkCode(build)
        window.history.pushState(permalink, build.legion?.name + " " + build.currentLevel.toString(), "/?b=" + permalink)
        js("if(gtag){gtag('config', 'UA-42953114-2', {'page_path': '/?b='+ permalink})}")
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
            a(href = "http://steamcommunity.com/games/469600/announcements/detail/3271170356004951599", classes = "btn btn-outline-primary mr-2", target = "_blank") {
                +"1.67"
            }
            a(href = "https://github.com/attrib/legion2-builder", classes = "btn btn-outline-success mr-2", target = "_blank") {
                +"Github"
            }
        }

        if (state.errorMessage != null) {
            div("error") {
                p {
                    +state.errorMessage.toString()
                }
                attrs.onClickFunction = {
                    setState {
                        errorMessage = null
                    }
                }
            }
        }

        if (state.uploadingFile) {
            div("loading") { +"Loading" }
        } else {
            document.onkeyup = {
                val event = it as KeyboardEvent
                val allowedKeysGeneral = listOf("+", "-", "Tab")
                val allFightersKey = listOf("q", "w", "e", "r", "t", "y", "u")
                val buildableFighters = LegionData.buildableFighters(state.build.legion).mapIndexed { index: Int, unitDef: UnitDef -> allFightersKey[index] to unitDef }.toMap()
                if (allowedKeysGeneral.contains(event.key) || buildableFighters.containsKey(event.key)) {
                    event.preventDefault()
                    setState {
                        when (event.key) {
                            "+" -> build.levelIncrease()
                            "-" -> build.levelDecrease()
                            "Tab" -> selectedTab = if (selectedTab == Tabs.WaveEditor) Tabs.BuildOrder else Tabs.WaveEditor
                            else -> selectedUnit.select(buildableFighters[event.key]!!)
                        }
                    }
                }
            }

            div("header") {
                div("container") {
                    div("row") {
                        div("col") {
                            legionSelect(state.build, state.replayResult, state.selectedPlayer, object : LegionSelectEventHandler {
                                override fun reset() {
                                    setState {
                                        resetBuild()
                                    }
                                }

                                override fun replayFileSelected() {
                                    setState {
                                        uploadingFile = true
                                    }
                                }

                                override fun replayFileLoaded(replayResult: ReplayResult) {
                                    setState {
                                        resetBuild()
                                        this.replayResult = replayResult
                                        uploadingFile = false
                                    }
                                }

                                override fun replayPlayerSeleceted(player: String) {
                                    setState {
                                        build = state.replayResult?.playerBuilds?.get(player)!!
                                        selectedTab = Tabs.BuildOrder
                                        updateHistory()
                                    }
                                }

                                override fun changeLegion(legion: Legion) {
                                    setState {
                                        build.legionId = legion.id
                                        build.legion = legion
                                    }
                                }

                            })
                        }
                        div("col") {
                            laneInfo(state.build)
                        }
                        div("col") {
                            buildInfo(state.build)
                        }
                        div("col") {
                            val waveDef = LegionData.getWaveCreaturesDef(state.build.currentLevel)
                            waveInfo(waveDef, state.build.currentLevel, object : LevelInterface {
                                override fun decrease() {
                                    setState {
                                        state.build.levelDecrease()
                                        updateHistory()
                                    }
                                }

                                override fun increase() {
                                    setState {
                                        state.build.levelIncrease()
                                        updateHistory()
                                    }
                                }

                            })
                        }
                    }
                }
            }

            div("tab-bar") {
                div("container") {
                    div("row justify-content-start") {
                        div("col-auto") {
                            +"Wave editor"
                            if (state.selectedTab == Tabs.WaveEditor) {
                                attrs.classes += "active"
                            }
                            attrs.onClickFunction = {
                                setState { selectedTab = Tabs.WaveEditor }
                            }
                        }
                        div("col-auto") {
                            +"Build order"
                            if (state.selectedTab == Tabs.BuildOrder) {
                                attrs.classes += "active"
                            }
                            attrs.onClickFunction = {
                                setState { selectedTab = Tabs.BuildOrder }
                            }
                        }
                    }
                }
            }

            div("container") {
                attrs.classes += state.selectedTab.toString()
                when (state.selectedTab) {
                    Tabs.WaveEditor -> {
                        waveEditor(state.build, state.selectedUnit, object : WaveEditorEventHandler {
                            override fun addResearch(researchDef: ResearchDef) {
                                setState {
                                    build.addResearch(researchDef)
                                    updateHistory()
                                }
                            }

                            override fun removeResearch(research: Research) {
                                setState {
                                    build.removeResearch(research)
                                    updateHistory()
                                }
                            }

                            override fun addFighter(unit: UnitDef, x: Int, y: Int) {
                                setState {
                                    build.addFighter(unit, Position(x, y))
                                    updateHistory()
                                }
                            }

                            override fun selectNewFighter(unitDef: UnitDef) {
                                setState {
                                    selectedUnit.select(unitDef)
                                }
                            }

                            override fun addMercenary(unitDef: UnitDef) {
                                setState {
                                    build.addMerchenary(unitDef)
                                    updateHistory()
                                }
                            }

                            override fun removeMercenary(unit: UnitState) {
                                setState {
                                    build.removeMerchenary(unit)
                                    updateHistory()
                                }
                            }

                            override fun selectUnit(unit: UnitState) {
                                setState {
                                    selectedUnit.select(unit)
                                }
                            }

                            override fun deselect() {
                                setState { this.selectedUnit.clearSelection() }
                            }

                            override fun recall() {
                                setState {
                                    if (selectedUnit.isBuiltUnit()) {
                                        build.removeFighter(selectedUnit.getBuiltUnit())
                                        selectedUnit.clearSelection()
                                        updateHistory()
                                    }
                                }
                            }

                            override fun undeploy() {
                                setState {
                                    if (selectedUnit.isBuiltUnit()) {
                                        build.sellFighter(selectedUnit.getBuiltUnit())
                                        selectedUnit.clearSelection()
                                        updateHistory()
                                    }
                                }
                            }

                            override fun upgrade(upgradeTo: UnitDef) {
                                setState {
                                    if (selectedUnit.isBuiltUnit()) {
                                        selectedUnit.select(build.upgradeFighter(selectedUnit.getBuiltUnit(), upgradeTo))
                                        updateHistory()
                                    }
                                }
                            }

                            override fun downgrade() {
                                setState {
                                    if (selectedUnit.isBuiltUnit()) {
                                        val downgradedUnit = build.downgradeFigther(selectedUnit.getBuiltUnit());
                                        if (downgradedUnit !== null) {
                                            selectedUnit.select(downgradedUnit)
                                            updateHistory()
                                        }
                                    }
                                }
                            }

                        })
                    }
                    Tabs.BuildOrder -> {
                        buildOrder(state.build, state.selectedUnit, object : BuildOrderEventHandler {
                            override fun selectLevel(level: Int) {
                                setState {
                                    build.currentLevel = level
                                    selectedTab = Tabs.WaveEditor
                                }
                            }
                        })
                    }
                }
            }
            footer("container") {
                +"Images and data are property of AutoAttack Games, Inc."
                br { }
                +"Legion TD, and Legion TD 2 are registered trademarks of AutoAttack Games, Inc."
            }
        }
    }
}

fun RBuilder.app() = child(App::class) {}
