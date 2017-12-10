package builder.ui

import builder.data.Unit
import builder.data.UnitClass
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.div
import react.dom.h4
import react.dom.img
import react.dom.p

interface UnitEventHandler {
    fun onClick(unit: Unit)
}

fun RBuilder.unitUi(id: String, unit: Unit, unitEventHandler: UnitEventHandler) {
    div("unit") {
        attrs.id = id
        attrs.onClickFunction = {
            unitEventHandler.onClick(unit)
        }
        img(alt = unit.name, src = unit.iconpath) {
            attrs {
                width = "64px"
                height = "64px"
            }
        }
        div("unit-info") {
            h4 { +unit.name }
            p {
                +"HP: "
                +unit.hp.toString()
            }
            p {
                +"DPS: "
                +(unit.dps ?: 0.0).toString()
            }
            p {
                +"Costs: "
                +when (unit.unitClass) {
                    UnitClass.Worker -> (unit.totalvalue ?: 0).toString()
                    UnitClass.Fighter -> (unit.totalvalue ?: 0).toString()
                    UnitClass.Mercenary -> (unit.mythiumcost ?: 0).toString()
                    else -> ""
                }
            }
            if (unit.unitClass == UnitClass.Fighter || unit.unitClass == UnitClass.Worker) {
                p {
                    +"Food: "
                    +(unit.totalfood ?: 0).toString()
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