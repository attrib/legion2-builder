package builder.ui

import kotlinext.js.invoke
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import ltd2.ResearchDef
import react.RBuilder
import react.dom.div
import react.dom.h4
import react.dom.p

fun RBuilder.researchUi(researchDef: ResearchDef, callback: () -> Unit, additionalClasses: String? = null, upgradeLevel: Int = 0) {
    kotlinext.js.require("builder/ui/unitUi.css")

    div("unit tooltip-parent") {
        if (additionalClasses != null) {
            attrs.classes += additionalClasses
        }
        attrs.onClickFunction = {
            callback()
        }
        IconImg( researchDef.iconPath, researchDef.name, 64, 64)
        div("tooltip-data") {
            h4 { +researchDef.name }
            p {
                +researchDef.tooltip
            }
            p {
                +" Gold Costs: "
                +(researchDef.goldCost + upgradeLevel * researchDef.goldCostPerLevel).toString()
            }
            p {
                +" Mythium Costs: "
                +(researchDef.mythiumCost + upgradeLevel * researchDef.mythiumCostPerLevel).toString()
            }
        }
    }
}