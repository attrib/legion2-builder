package app

import builder.Build
import builder.Game
import builder.GameEventHandler
import builder.data.Unit
import react.*
import react.dom.*
import logo.*

//import ticker.*

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
                select {
                    for (legionId in state.game.legions.keys) {
                        if (!state.game.legions[legionId]!!.isPlayable()) {
                            continue
                        }
                        option {
                            attrs.value = legionId
                            if (state.build.legionId == legionId) {
                                attrs.selected = true
                            }
                            +state.game.legions[legionId]!!.name
                        }
                    }
                }
            }
            div {
                h3 { +"Build info" }
                p {
                    +"Level: "
                    +state.build.currentLevel.toString()
                }
                p {
                    +"Cost: "
                    +state.build.costs.toString()
                }
                p {
                    +"Available: "
                    +state.build.available.toString()
                }
            }
            div {
                h3 { +"Actions" }
                button {
                    +"+"
                }
                button {
                    +"-"
                }
                button {
                    +"reset"
                }
                a(href = "") {
                    +"Link"
                }
            }
        }

        div("area") {
            div {
                h2 { +"Lane" }
                div("lane") {
                    div {
                        h3 { +"build area" }
                    }
                    div {
                        h3 { +"available" }

                        if (state.build.legion == null) {
                            +"Please select legion"
                        } else {
                            ul {
                                for (unit in state.build.legion!!.creatures) {
                                    li { unitUi(unit) }
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
                    }
                    div {
                        h3 { +"available" }
                        ul {
                            for (unit in state.game.mercenaries) {
                                li { unitUi(unit) }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.app() = child(App::class) {}

fun RBuilder.unitUi(unit: Unit) {
    div("unit") {
        +unit.name
        div("unit-info") {
            p {
                +"HP: "
                +unit.hp
            }
            p {
                +"DPS: "
                +unit.dps
            }
            p {
                +"Costs: "
                +unit.goldcost
            }
            p {
                +"AttackType: "
                +unit.attackType.toString()
            }
            p {
                +"DefenseType: "
                +unit.armorType.toString()
            }
        }
    }
}