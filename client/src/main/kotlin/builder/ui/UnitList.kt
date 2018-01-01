package builder.ui

import builder.data.UnitSelection
import ltd2.*
import react.RBuilder
import react.dom.li
import react.dom.ul

interface UnitListEventHandler {
    fun click(unit: Any)
}

fun <k:Any>RBuilder.unitList(units: List<k>, clickCallback: (Any) -> Unit, selectedUnit: UnitSelection, researches: List<Research> = listOf()) {
    val eventHandler = object : UnitListEventHandler {
        override fun click(unit: Any) {
            clickCallback(unit)
        }
    }

    if (units.isNotEmpty()) {
        ul("list-inline row no-gutters justify-content-start") {
            units.forEach { unit ->
                li("col-auto") {
                    val addClass = if (selectedUnit.isSelected(unit)) "selected" else ""
                    when (unit) {
                        is UnitDef -> unitUi(unit, { eventHandler.click(unit) }, addClass)
                        is UnitState -> unitUi(unit.def, { eventHandler.click(unit) }, addClass)
                        is Research -> researchUi(unit.def, { eventHandler.click(unit) }, addClass, unit.upgradeLevel)
                        is ResearchDef -> {
                            val upgradeLevel = researches.filter { it.def.id == unit.id }.size
                            researchUi(unit, { eventHandler.click(unit) }, addClass, upgradeLevel)
                        }
                    }
                }
            }
        }
    }
}