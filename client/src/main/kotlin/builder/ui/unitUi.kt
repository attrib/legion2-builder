package builder.ui

import app.format
import kotlinext.js.invoke
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import ltd2.UnitClass
import ltd2.UnitDef
import react.RBuilder
import react.dom.div
import react.dom.h4
import react.dom.p

fun RBuilder.unitUi(unit: UnitDef, callback: () -> Unit, additionalClasses: String? = null) {
    kotlinext.js.require("builder/ui/unitUi.css")

    div("unit tooltip-parent") {
        if (additionalClasses != null) {
            attrs.classes += additionalClasses
        }
        attrs.onClickFunction = {
            callback()
        }
        IconImg( unit.iconPath, unit.name, 64, 64)
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
                    UnitClass.Worker -> (unit.goldCost).toString()
                    UnitClass.Fighter -> (unit.goldCost).toString()
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