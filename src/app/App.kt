package app

import builder.Build
import builder.Game
import builder.data.Resistance
import builder.ui.dpsUi
import builder.ui.hpUi
import builder.ui.unitUi
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
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
        build = Build(game, game.data.global)
        build.legion = game.data.legionsMap["element_legion_id"]!!
        build.legionId = "element_legion_id"
    }

    override fun RBuilder.render() {
        div("App-header") {
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
                    state.game.data.legionsMap.forEach { (legionId, legion) ->
                        if (!legion.playable) {
                            return@forEach
                        }
                        input(type = InputType.radio, name = "legion") {
                            attrs.onChangeFunction = {
                                val target = it.target as HTMLInputElement
                                setState {
                                    build.legionId = target.value
                                    build.legion = game.data.legionsMap[target.value]
                                }
                            }
                            attrs.value = legionId
                            attrs.id = "select-" + legionId
                            if (state.build.legionId == legionId) {
                                attrs.checked = true
                            }
                        }
                        label {
                            attrs.htmlFor = "select-" + legionId
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
                val unitId = state.game.data.waves[state.build.currentLevel].unit
                val unitDef = state.game.data.unitsMap[unitId]
                val waveDef = state.game.getWaveCreaturesDef(state.build.currentLevel)

                p {
                    +"Total HP: ${state.build.totalHp}"
                    hpUi(state.build.getResistance(unitDef))
                }
                p {
                    +"Total DPS: ${state.build.totalDps.format(2)}"
                    dpsUi(state.build.getResistance(unitDef))
                }
                p {
                    +"Survivability Chance: ${state.build.survivability(waveDef)}"
                }
                p {
                    +"Workers: ${state.build.getWorkerCount()}"
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
                    +(state.build.currentLevel + 1).toString()
                }
                p {
                    +"Total HP: "
                    +waveDef.sumBy { it.hitpoints }.toString()
                    hpUi(Resistance(waveDef, state.game.data.global, null))
                }
                p {
                    +"Total DPS: "
                    +waveDef.sumByDouble { it.dmgBase * it.attackSpeed }.format(2)
                    dpsUi(Resistance(waveDef, state.game.data.global, null))
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
                                state.build.getFighters(true).forEach { unit ->
                                    li {
                                        unitUi(unit.def, { setState { build.removeFighter(unit) } })
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
                                state.game.fighters(state.build.legion!!).forEach { unit ->
                                    if (!unit.id.startsWith("test")) {
                                        li {
                                            unitUi(unit, { setState { build.addFighter(unit) } })
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
                                state.build.getMerchenaries().forEach { unit ->
                                    li {
                                        unitUi(unit.def, { setState { build.removeMerchenary(unit) } })
                                    }
                                }
                            }
                        }
                    }
                    div("selection") {
                        h3 { +"available" }
                        ul {
                            state.game.mercenaries().forEach { unit ->
                                if (!unit.id.startsWith("test")) {
                                    li {
                                        unitUi(unit, { setState { build.addMerchenary(unit) } })
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