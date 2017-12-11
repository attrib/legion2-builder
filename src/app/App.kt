package app

import builder.Build
import builder.Game
import builder.GameEventHandler
import builder.data.Resistance
import builder.data.Unit
import builder.ui.UnitEventHandler
import builder.ui.dpsUi
import builder.ui.hpUi
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
        build = Build(game.waves, game.globals["global_default"]!!)
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

        p {
            +"All images and data is copyrighted by "
            a("http://legiontd2.com") {
                +"Legion 2 TD"
            }
            +". For the source code see "
            a(href = "https://github.com/attrib/legion2-builder") {
                +"https://github.com/attrib/legion2-builder"
            }
            +"."
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
                    hpUi(state.build.resistance)
                }
                p {
                    +"Total DPS: "
                    +state.build.totalDps.format(2)
                    dpsUi(state.build.resistance)
                }
                p {
                    +"Survivability Chance: "
                    +state.build.survivability(state.game.getWave(state.build.currentLevel)!!)
                }
                p {
                    +"Workers: "
                    +state.build.getWorkerCount().toString()
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
                val wave = state.game.getWave(state.build.currentLevel)!!
                h3 { +"Wave Info" }
                p {
                    +"Level: "
                    +state.build.currentLevel.toString()
                }
                p {
                    +"Total HP: "
                    +wave.totalHp.toString()
                    hpUi(Resistance(wave.creatures))
                }
                p {
                    +"Total DPS: "
                    +wave.totalDps.format(2)
                    dpsUi(Resistance(wave.creatures))
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
                        if (state.build.getFighters(true).isNotEmpty()) {
                            ul {
                                for ((index, unit) in state.build.getFighters(true)) {
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
                    div("selection") {
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
                    div("selection") {
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