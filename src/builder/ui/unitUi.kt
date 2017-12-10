package builder.ui

import builder.data.Unit
import builder.data.UnitClass
import kotlinx.html.id
import react.RBuilder
import react.dom.div
import react.dom.p

fun RBuilder.unitUi(id: String, unit: Unit) {
    div("unit") {
        attrs.id = id
        +unit.name
        div("unit-info") {
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
                    UnitClass.Fighter -> (unit.totalvalue ?: 0).toString()
                    UnitClass.Mercenary -> (unit.mythiumcost ?: 0).toString()
                    else -> ""
                }
            }
            if (unit.unitClass == UnitClass.Fighter) {
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