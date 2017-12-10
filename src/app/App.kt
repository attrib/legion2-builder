package app

import builder.Build
import builder.Game
import builder.GameEventHandler
import builder.data.Unit
import builder.data.UnitClass
import kotlinx.html.id
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
                    build.legionId = "element_legion_id";
                    build.legion = game.legions["element_legion_id"];
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
                    for ((legionId, legion) in state.game.legions) {
                        if (!legion.isPlayable()) {
                            continue
                        }
                        option {
                            attrs.value = legionId
                            if (state.build.legionId == legionId) {
                                attrs.selected = true
                            }
                            +legion.name
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
                                for ((id, unit) in state.build.legion!!.fighters) {
                                    if (unit.isEnabled) {
                                        li { unitUi(id, unit) }
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
                    }
                    div {
                        h3 { +"available" }
                        ul {
                            for ((id, unit) in state.game.mercenaries) {
                                if (unit.isEnabled) {
                                    li { unitUi(id, unit) }
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

fun RBuilder.unitUi(id: String, unit: Unit) {
    div("unit") {
        attrs.id = id
        +unit.name
        div("unit-info") {
            p {
                +"HP: "
                +unit.hp.toString()
            }
            p {
                +"DPS: "
                +(unit.dps ?: 0.0).toString()
            }
            p {
                +"Costs: "
                +when (unit.unitClass) {
                    UnitClass.Fighter -> (unit.totalvalue ?: 0).toString()
                    UnitClass.Mercenary -> (unit.mythiumcost ?: 0).toString()
                    else -> ""
                }
            }
            if (unit.unitClass == UnitClass.Fighter) {
                p {
                    +"Food: "
                    +(unit.totalfood ?: 0).toString()
                }
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