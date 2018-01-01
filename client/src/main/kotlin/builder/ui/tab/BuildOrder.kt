package builder.ui.tab

import builder.data.UnitSelection
import ltd2.Build
import ltd2.LegionData
import builder.ui.unitList
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.*

interface BuildOrderEventHandler {
    fun selectLevel(level: Int)
}

fun RBuilder.buildOrder(build: Build, selectedUnit: UnitSelection, eventHandler: BuildOrderEventHandler) {
    table("table table-striped") {
        thead {
            tr {
                th { +"#" }
                th { +"Unit" }
                th { +"Research" }
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
                    td { unitList(fighters.fighters(), {}, selectedUnit) }
                    td { unitList(build.getAllResearches().filter { it.buildLevel == currentLevel }, {}, selectedUnit) }
                    td { unitList(build.getMerchenaries(currentLevel), {}, selectedUnit) }
                    td { +"${fighters.totalValue()}" }
                    td { +"${build.getValueByLevel(currentLevel)} / ${wave.recommendedValue}" }
                }
            }
        }
    }
}