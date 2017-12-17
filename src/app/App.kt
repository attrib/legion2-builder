package app

import builder.*
import builder.data.Unit
import builder.data.isEnabled
import builder.ui.*
import parser.ReplayResult
import react.*
import react.dom.*

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

interface AppState : RState {
    var build: Build
    var selectedUnit: Unit?
    var selectedPlayer: String?
    var replayResult: ReplayResult?
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

            div("container") {
                div("row") {
                    div("col-8") {
                        buildArea(state.build, state.selectedUnit, object : BuildAreaEventHandler {
                            override fun selectUnit(unit: Unit) {
                                setState {
                                    selectedUnit = if (selectedUnit == unit) null else unit
                                }
                            }

                        })
                    }
                    aside("col-4") {

                        selectedUnitInfo(state.selectedUnit, state.build, object : SelectedUnitInfoEventHandler {
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

                        div {
                            if (state.build.legion == null) {
                                +"Please select legion"
                            } else {
                                unitList(LegionData.fighters(state.build.legion!!) + LegionData.upgrades(), { unit ->
                                    unit.isEnabled() && unit.upgradesFrom == null
                                }, { unit ->
                                    setState { selectedUnit = build.addFighter(unit) }
                                })
                            }
                        }

                        div {
                            h2 { +"Mercenaries" }
                            div {
                                div {
                                    unitList(state.build.getMerchenaries(), { true }, { setState { build.removeMerchenary(it) } })
                                }
                                div {
                                    unitList(LegionData.mercenaries(), { it.isEnabled() }, { setState { build.addMerchenary(it) } })
                                }
                            }
                        }

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