package app

import builder.Build
import builder.Game
import builder.Legion
import builder.data.Resistance
import builder.ui.UnitEventHandler
import builder.ui.dpsUi
import builder.ui.hpUi
import builder.ui.unitUi
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import logo.logo
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*

fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits)

interface AppState : RState {
    var build: Build
    var game: Game
}

class App : RComponent<RProps, AppState>() {

    override fun AppState.init() {
        game = Game()
        resetBuild()
    }

    fun AppState.resetBuild() {
        build = Build(game, game.globals["global_default"]!!)
        build.legion = game.legions[Legion.Element]
        build.legionId = Legion.Element
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

        div("info") {
            div {
                h3 { +"Select Legion" }
                p {
                    for ((legionId, legion) in state.game.legions) {
                        if (!legion.playable) {
                            continue
                        }
                        input(type = InputType.radio, name = "legion") {
                            attrs.onChangeFunction = {
                                val target = it.target as HTMLInputElement
                                setState {
                                    build.legionId = Legion.valueOf(target.value)
                                    build.legion = game.legions[build.legionId!!]
                                }
                            }
                            attrs.value = legionId.toString()
                            attrs.id = "select-" + legionId.toString()
                            if (state.build.legionId == legionId) {
                                attrs.checked = true
                            }
                        }
                        label {
                            attrs.htmlFor = "select-" + legionId.toString()
                            +legion.name
                        }
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
                    hpUi(state.build.getResistance(state.game.creatures[state.game.waves[state.build.currentLevel]?.unit]))
                }
                p {
                    +"Total DPS: "
                    +state.build.totalDps.format(2)
                    dpsUi(state.build.getResistance(state.game.creatures[state.game.waves[state.build.currentLevel]?.unit]))
                }
                p {
                    val waveDef = state.game.getWaveCreaturesDef(state.build.currentLevel)
                    +"Survivability Chance: "
                    +state.build.survivability(waveDef)
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
                val waveDef = state.game.getWaveCreaturesDef(state.build.currentLevel)
                h3 { +"Wave Info" }
                p {
                    +"Level: "
                    +state.build.currentLevel.toString()
                }
                p {
                    +"Total HP: "
                    +waveDef.sumBy { it.hitpoints }.toString()
                    hpUi(Resistance(waveDef, state.game.globals["global_default"]!!, null))
                }
                p {
                    +"Total DPS: "
                    +waveDef.sumByDouble { it.dmgBase * it.attackSpeed }.format(2)
                    dpsUi(Resistance(waveDef, state.game.globals["global_default"]!!, null))
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
                                for (unit in state.build.getFighters(true)) {
                                    li {
                                        unitUi(unit.def, object : UnitEventHandler {
                                            override fun onClick() {
                                                setState {
                                                    build.removeFighter(unit)
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
                                    if (!unit.id.startsWith("test")) {
                                        li {
                                            unitUi(unit, object : UnitEventHandler {
                                                override fun onClick() {
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
                                for (unit in state.build.getMerchenaries()) {
                                    li {
                                        unitUi(unit.def, object : UnitEventHandler {
                                            override fun onClick() {
                                                setState {
                                                    build.removeMerchenary(unit)
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
                                if (!unit.id.startsWith("test")) {
                                    li {
                                        unitUi(unit, object : UnitEventHandler {
                                            override fun onClick() {
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