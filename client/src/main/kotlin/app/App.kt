package app

import builder.PermaLinkV1JS
import builder.getWaveCreaturesDef
import builder.ui.header.*
import builder.ui.tab.BuildOrderEventHandler
import builder.ui.tab.WaveEditorEventHandler
import builder.ui.tab.buildOrder
import builder.ui.tab.waveEditor
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import ltd2.*
import org.w3c.dom.PopStateEvent
import org.w3c.dom.events.Event
import org.w3c.dom.get
import parser.ReplayResult
import react.*
import react.dom.*
import kotlin.browser.window

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

enum class Tabs {
    BuildOrder,
    WaveEditor
}

interface AppState : RState {
    var build: Build
    var selectedUnit: UnitState?
    var selectedPlayer: String?
    var replayResult: ReplayResult?
    var uploadingFile: Boolean
    var selectedTab: Tabs
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        uploadingFile = false
        selectedTab = Tabs.WaveEditor
        replayResult = null
        val url = window.location.href
        if (url.contains("?b=")) {
            val code = url.split("?b=")[1]
            build = PermaLinkV1JS.fromPermaLinkCode(code)
        }
        else {
            resetBuild()
        }
        window.onpopstate = { event: Event ->
            val state = (event as PopStateEvent).state
            if (state !== null) {
                setState {
                    build = PermaLinkV1JS.fromPermaLinkCode(state.toString())
                }
            }
        }
    }

    fun AppState.resetBuild() {
        build = Build()
        build.legion = LegionData.legionsMap["element_legion_id"]!!
        build.legionId = "element_legion_id"
        selectedUnit = null
        updateHistory()
    }

    fun AppState.updateHistory() {
        val permalink = PermaLinkV1JS.toPermaLinkCode(build)
        window.history.pushState(permalink, build.legion?.name + " " + build.currentLevel.toString(), "/?b=" + permalink)
        if (jsTypeOf(window["gtag"]) !== "undefined") {
            window["gtag"]("set", "page", "/?b=" + permalink)
            window["gtag"]("send", "pageview", "/?b=" + permalink)
        }
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
            a(href = "http://steamcommunity.com/games/469600/announcements/detail/1601458706072267291", classes = "btn btn-outline-primary mr-2", target = "_blank") {
                +"1.66"
            }
            a(href = "https://github.com/attrib/legion2-builder", classes = "btn btn-outline-success mr-2", target = "_blank") {
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
                            override fun addFighter(unitDef: UnitDef) {
                                setState {
                                    selectedUnit = build.addFighter(unitDef)
                                    updateHistory()
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
                                    selectedUnit = if (selectedUnit == unit) null else unit
                                }
                            }

                            override fun deselect() {
                                setState { this.selectedUnit = null }
                            }

                            override fun recall() {
                                setState {
                                    build.removeFighter(selectedUnit!!)
                                    selectedUnit = null
                                    updateHistory()
                                }
                            }

                            override fun undeploy() {
                                setState {
                                    build.sellFighter(selectedUnit!!)
                                    selectedUnit = null
                                    updateHistory()
                                }
                            }

                            override fun upgrade(upgradeTo: UnitDef) {
                                setState {
                                    selectedUnit = build.upgradeFighter(selectedUnit!!, upgradeTo)
                                    updateHistory()
                                }
                            }

                        })
                    }
                    Tabs.BuildOrder -> {
                        buildOrder(state.build, object : BuildOrderEventHandler {
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