package app

import builder.*
import builder.data.Unit
import builder.ui.*
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import index.LZString
import kotlinx.html.id
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
    var selectedUnit: Unit?
    var selectedPlayer: String?
    var replayResult: ReplayResult?
    var uploadingFile: Boolean
    var selectedTab: Tabs
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        resetBuild()
        uploadingFile = false
        selectedTab = Tabs.WaveEditor
        replayResult = null
        val url = window.location.href
        if( url.contains("?b=") ) {
            val code = url.split("?b=")[1]
            val s = LZString.decompressFromBase64(code)
            val arr = fromString(s).buffer
            val ds2 = DSFactory.DataStream(arr)
            build = Build()
            build.load(ds2)
        }
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
                                    setState { state.build.levelDecrease() }
                                }

                                override fun increase() {
                                    setState { state.build.levelIncrease() }
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
                                setState { selectedUnit = build.addFighter(unitDef) }
                            }

                            override fun addMercenary(unitDef: UnitDef) {
                                setState { build.addMerchenary(unitDef) }
                            }

                            override fun removeMercenary(unit: Unit) {
                                setState { build.removeMerchenary(unit) }
                            }

                            override fun selectUnit(unit: Unit) {
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
                                }
                            }

                            override fun undeploy() {
                                setState {
                                    build.sellFighter(selectedUnit!!)
                                    selectedUnit = null
                                }
                            }

                            override fun upgrade(upgradeTo: UnitDef) {
                                setState { selectedUnit = build.upgradeFighter(selectedUnit!!, upgradeTo) }
                            }

                        })
                    }
                    Tabs.BuildOrder -> {
                        buildOrder(state.build)
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