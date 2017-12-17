package builder.ui.header

import builder.Build
import builder.LegionData
import react.RBuilder
import react.dom.div
import react.dom.h3

fun RBuilder.buildInfo(build: Build) {
    h3 { +"Build info" }
    div {
        +"Cost: "
        +build.costs.toString()
        +" / "
        +LegionData.waves[build.currentLevel].recommendedValue.toString()
    }
    div {
        +"Food: "
        +build.foodCosts.toString()
//                            +" / "
//                            +"15"
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