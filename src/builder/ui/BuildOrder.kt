package builder.ui

import builder.Build
import builder.LegionData
import react.RBuilder
import react.dom.*

fun RBuilder.buildOrder(build: Build) {
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
                    td { +wave.levelNum.toString() }
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