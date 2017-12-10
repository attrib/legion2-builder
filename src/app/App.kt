package app

import builder.Build
import builder.Game
import builder.GameEventHandler
import builder.ui.unitUi
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import logo.logo
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*

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