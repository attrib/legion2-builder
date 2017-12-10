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
                    resetBuild()
                }
            }
        })
    }

    fun AppState.resetBuild() {
        build = Build(game.waves)
        build.legionId = "element_legion_id"
        build.legion = game.legions["element_legion_id"]
    }

    override fun RBuilder.render() {
        div("App-header") {
            logo()
            h1 {
                +"Welcome to Legion TD 2 Builder"
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
                            resetBuild()
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
                    +state.build.totalHp.toString()
                }
                p {
                    +"Total DPS: "
                    +state.build.totalDps.format(2)
                }
                p {
                    +"Survivability Chance: "
                    +state.build.survivability(state.game.getWave(state.build.currentLevel)!!)
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
                p {
                    +"Income: "
                    +state.build.income.toString()
                }
            }
            div {
                h3 { +"Wave Info" }
                p {
                    +"Level: "
                    +state.build.currentLevel.toString()
                }
                p {
                    +"Total HP: "
                    +state.game.getWave(state.build.currentLevel)!!.totalHp.toString()
                }
                p {
                    +"Total DPS: "
                    +state.game.getWave(state.build.currentLevel)!!.totalDps.format(2)
                }
                button {
                    +"+"
                    attrs.onClickFunction = {
                        setState { state.build.levelIncrease() }
                    }
                }
                button {
                    +"-"
                    attrs.onClickFunction = {
                        setState { state.build.levelDecrease() }
                    }
                }
            }
        }

        div("area") {
            div {
                h2 { +"Lane" }
                div("lane") {
                    div {
                        h3 { +"build area" }
                        if (state.build.getFighters().isNotEmpty()) {
                            ul {
                                for ((index, unit) in state.build.getFighters()) {
                                    li {
                                        unitUi("", unit, object : UnitEventHandler {
                                            override fun onClick(unit: Unit) {
                                                setState {
                                                    build.removeFighter(index)
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
                                                        build.addFighter(unit)
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
                        if (state.build.getMerchenaries().isNotEmpty()) {
                            ul {
                                for ((index, unit) in state.build.getMerchenaries()) {
                                    li {
                                        unitUi("", unit, object : UnitEventHandler {
                                            override fun onClick(unit: Unit) {
                                                setState {
                                                    build.removeMerchenary(index)
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
                                                    build.addMerchenary(unit)
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