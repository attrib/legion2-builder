package builder.ui.tab

import builder.Build
import builder.LegionData
import builder.ui.unitList
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.*

interface BuildOrderEventHandler {
    fun selectLevel(level: Int)
}

fun RBuilder.buildOrder(build: Build, eventHandler: BuildOrderEventHandler) {
    table("table table-striped") {
        thead {
            tr {
                th { +"#" }
                th { +"Unit" }
                th { +"Workers" }
                th { +"Mercenary" }
                th { +"Gold needed" }
                th { +"Value" }
            }
        }
        tbody {
            val allFighters = build.getFightersUnfiltered()
            LegionData.waves.forEach { wave ->
                val currentLevel = wave.levelNum - 1
                val fighters = allFighters.forLevel(currentLevel)
                tr {
                    td { a("#") {
                        +wave.levelNum.toString()
                        attrs.onClickFunction = {
                            eventHandler.selectLevel(currentLevel)
                        }
                    }}
                    td { unitList(fighters.fighters(), { true }, {}) }
                    td { if (fighters.worker().size > 0) +"${fighters.worker().size}" else +"" }
                    td { unitList(build.getMerchenaries(currentLevel), { true }, {}) }
                    td { +"${fighters.totalValue()}" }
                    td { +"${build.getValueByLevel(currentLevel)} / ${wave.recommendedValue}" }
                }
            }
        }
    }
}