package builder.ui

import builder.data.Unit
import builder.UnitClass
import builder.UnitDef
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.div
import react.dom.h4
import react.dom.img
import react.dom.p

interface UnitEventHandler {
    fun onClick()
}

fun RBuilder.unitUi(unit: UnitDef, unitEventHandler: UnitEventHandler) {
    div("unit") {
        attrs.onClickFunction = {
            unitEventHandler.onClick()
        }
        img(alt = unit.id, src = "") {
            attrs {
                width = "64px"
                height = "64px"
            }
        }
        div("unit-info") {
            h4 { +unit.id }
            p {
                +"HP: "
                +unit.hitpoints.toString()
            }
            p {
                +"DPS: "
                +(unit.dmgBase * unit.attackSpeed).toString()
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