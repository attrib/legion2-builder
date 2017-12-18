package builder.ui

import app.format
import ltd2.UnitClass
import ltd2.UnitDef
import kotlinext.js.invoke
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.div
import react.dom.h4
import react.dom.img
import react.dom.p

interface UnitEventHandler {
    fun onClick()
}

fun RBuilder.unitUi(unit: UnitDef, callback: () -> Unit, additionalClasses: String? = null) {
    kotlinext.js.require("builder/ui/unitUi.css")

    val unitEventHandler = object : UnitEventHandler {
        override fun onClick() {
            callback()
        }
    }
    div("unit tooltip-parent") {
        if (additionalClasses != null) {
            attrs.classes += additionalClasses
        }
        attrs.onClickFunction = {
            unitEventHandler.onClick()
        }
        img(alt = unit.id, src = unit.iconPath.replace("Splashes", "Icons")) {
            attrs {
                width = "64px"
                height = "64px"
            }
        }
        div("tooltip-data") {
            h4 { +unit.name }
            p {
                +"HP: "
                +unit.hitpoints.toString()
            }
            p {
                +"DPS: "
                +(unit.dmgBase * unit.attackSpeed).format(2)
            }
            p {
                +"Costs: "
                +when (unit.unitClass) {
                    UnitClass.Worker -> (unit.totalValue).toString()
                    UnitClass.Fighter -> (unit.totalValue).toString()
                    UnitClass.Mercenary -> (unit.mythiumCost).toString()
                    else -> ""
                }
            }
            if (unit.unitClass == UnitClass.Fighter || unit.unitClass == UnitClass.Worker) {
                p {
                    +"Food: "
                    +(unit.totalFood).toString()
                }
            }
            p {
                +"AttackType: "
                +unit.attackType.toString()
            }
            p {
                +"DefenseType: "
                +unit.armorType.toString()
            }
        }
    }
}