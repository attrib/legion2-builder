package builder.ui.header

import ltd2.Build
import ltd2.LegionData
import react.RBuilder
import react.dom.div
import react.dom.h3

fun RBuilder.buildInfo(build: Build) {
    h3 { +"Build info" }
    div {
        +"Value: "
        +build.value.toString()
        +" / "
        +LegionData.waves[build.currentLevel].recommendedValue.toString()
    }
    div {
        +"Food: "
        +build.foodCosts.toString()
        +" / "
        +build.maxFood.toString()
    }
    div {
        +"Costs current level: "
        +build.getCostsForLevel(build.currentLevel).toString()
    }
    div {
        +"Available: "
        +build.available.toString()
    }
    div {
        +"Income: "
        +build.income.toString()
    }
}