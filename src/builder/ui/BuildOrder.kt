package builder.ui

import builder.Build
import builder.LegionData
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
                th { +"Mercenary" }
                th { +"Gold needed" }
                th { +"Value" }
            }
        }
        tbody {
            val allFighters = build.getFightersUnfiltered()
            LegionData.waves.forEach { wave ->
                val currentLevel = wave.levelNum - 1
                val fighters = allFighters.filter { it.buildLevel == currentLevel }
                tr {
                    td { a("#") {
                        +wave.levelNum.toString()
                        attrs.onClickFunction = {
                            eventHandler.selectLevel(currentLevel)
                        }
                    }}
                    td { unitList(fighters, { true }, {}) }
                    td { unitList(build.getMerchenaries(currentLevel), { true }, {}) }
                    td { +fighters.sumBy { it.def.goldCost }.toString() }
                    td {
                        +build.getCostsByLevel(currentLevel).toString()
                        +" / "
                        +wave.recommendedValue.toString()
                    }
                }
            }
        }
    }
}