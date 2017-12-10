package app

import builder.Build
import builder.Game
import builder.GameEventHandler
import builder.data.Unit
import builder.ui.UnitEventHandler
import builder.ui.unitUi
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import logo.logo
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*

//import ticker.*

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

interface AppState : RState {
    var build: Build
    var game: Game
    var loaded: Boolean
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        game = Game(object : GameEventHandler {
            override fun loaded() {
                setState {
                    loaded = true
                    build = Build()
                    build.legionId = "element_legion_id"
                    build.legion = game.legions["element_legion_id"]
                }
            }
        })
    }

    override fun RBuilder.render() {
        div("App-header") {
            logo()
            h1 {
                +"Welcome to Legion 2 Builder"
            }
        }

        if (!state.loaded) {
            div {
                +"Loading data"
            }
            return
        }

        div("info") {
            div {
                h3 { +"Select Legion" }
                p {
                    for ((legionId, legion) in state.game.legions) {
                        if (!legion.isPlayable()) {
                            continue
                        }
                        input(type = InputType.radio, name = "legion") {
                            attrs.onChangeFunction = {
                                val target = it.target as HTMLInputElement
                                setState {
                                    build.legionId = target.value
                                    build.legion = game.legions[build.legionId]
                                }
                            }
                            attrs.value = legionId
                            if (state.build.legionId == legionId) {
                                attrs.checked = true
                            }
                        }
                        +legion.name
                    }
                }
                button {
                    +"reset"
                    attrs.onClickFunction = {
                        setState {
                            build = Build()
                            build.legionId = "element_legion_id"
                            build.legion = game.legions["element_legion_id"]
                        }
                    }
                }
                a(href = "") {
                    +"Link"
                }

            }
            div {
                h3 { +"Lane info" }
                p {
                    +"Total HP: "
                    +state.build.lane.totalHp.toString()
                }
                p {
                    +"Total DPS: "
                    +state.build.lane.totalDps.format(2)
                }
            }
            div {
                h3 { +"Build info" }
                p {
                    +"Cost: "
                    +state.build.costs.toString()
                }
                p {
                    +"Food: "
                    +state.build.foodCosts.toString()
                }
                p {
                    +"Available: "
                    +state.build.available.toString()
                }
            }
            div {
                h3 { +"Wave Info" }
                p {
                    +"Level: "
                    +state.build.currentLevel.toString()
                }
                p {
                    +"Income: "
                    +state.build.income.toString()
                }
                button {
                    +"+"
                }
                button {
                    +"-"
                }
            }
        }

        div("area") {
            div {
                h2 { +"Lane" }
                div("lane") {
                    div {
                        h3 { +"build area" }
                        if (state.build.lane.fighters.size > 0) {
                            ul {
                                for (unit in state.build.lane.fighters) {
                                    li {
                                        unitUi("", unit, object : UnitEventHandler {
                                            override fun onClick(unit: Unit) {
                                                setState {
                                                    build.lane.fighters.remove(unit)
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }
                    div {
                        h3 { +"available" }

                        if (state.build.legion == null) {
                            +"Please select legion"
                        } else {
                            ul {
                                for ((id, unit) in state.build.legion!!.fighters) {
                                    if (unit.isEnabled) {
                                        li {
                                            unitUi(id, unit, object : UnitEventHandler {
                                                override fun onClick(unit: Unit) {
                                                    setState {
                                                        build.lane.fighters.add(unit)
                                                    }
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            div {
                h2 { +"Mercenaries" }
                div("lane") {
                    div {
                        h3 { +"selected" }
                        if (state.build.lane.mercenaries.size > 0) {
                            ul {
                                for (unit in state.build.lane.mercenaries) {
                                    li {
                                        unitUi("", unit, object : UnitEventHandler {
                                            override fun onClick(unit: Unit) {
                                                setState {
                                                    build.lane.mercenaries.remove(unit)
                                                }
                                            }
                                        })
                                    }
                                }
                            }
                        }
                    }
                    div {
                        h3 { +"available" }
                        ul {
                            for ((id, unit) in state.game.mercenaries) {
                                if (unit.isEnabled) {
                                    li {
                                        unitUi(id, unit, object : UnitEventHandler {
                                            override fun onClick(unit: Unit) {
                                                setState {
                                                    build.lane.mercenaries.add(unit)
                                                }
                                            }
                                        })
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

fun RBuilder.app() = child(App::class) {}