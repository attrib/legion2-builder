package builder.ui

import builder.data.UnitSelection
import ltd2.UnitDef
import ltd2.Units
import react.RBuilder
import react.dom.li
import react.dom.ul
import ltd2.UnitState as UnitInfo

interface UnitDefListEventHandler {
    fun click(unit: UnitDef)
}

interface UnitListEventHandler {
    fun click(unit: UnitInfo)
}

fun RBuilder.unitList(units: List<UnitDef>, filterCallback: (UnitDef) -> Boolean, clickCallback: (UnitDef) -> Unit, selectedUnit: UnitSelection) {
    val eventHandler = object : UnitDefListEventHandler {
        override fun click(unit: UnitDef) {
            clickCallback(unit)
        }
    }

    if (units.isNotEmpty()) {
        ul("list-inline row no-gutters justify-content-start") {
            units.filter { filterCallback(it) }.forEach { unit ->
                li("col-auto") {
                    val addClass = if (selectedUnit.isSelected(unit)) "selected" else ""
                    unitUi(unit, { eventHandler.click(unit) }, addClass)
                }
            }
        }
    }
}

fun RBuilder.unitList(units: Units, filterCallback: (UnitInfo) -> Boolean, clickCallback: (UnitInfo) -> Unit, selectedUnit: UnitSelection) {
    val eventHandler = object : UnitListEventHandler {
        override fun click(unit: UnitInfo) {
            clickCallback(unit)
        }
    }

    if (units.isNotEmpty()) {
        ul("list-inline row no-gutters justify-content-start") {
            units.filter { filterCallback(it) }.forEach { unit ->
                li("col-auto") {
                    val addClass = if (selectedUnit.isSelected(unit)) "selected" else ""
                    unitUi(unit.def, { eventHandler.click(unit) }, addClass)
                }
            }
        }
    }
}