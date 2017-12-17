package builder.ui

import builder.UnitDef
import react.RBuilder
import react.dom.li
import react.dom.ul
import builder.data.Unit as UnitInfo

interface UnitDefListEventHandler {
    fun click(unit: UnitDef)
}

interface UnitListEventHandler {
    fun click(unit: UnitInfo)
}

fun RBuilder.unitList(units: List<UnitDef>, filterCallback: (UnitDef) -> Boolean, clickCallback: (UnitDef) -> Unit) {
    val eventHandler = object : UnitDefListEventHandler {
        override fun click(unit: UnitDef) {
            clickCallback(unit)
        }
    }

    if (units.isNotEmpty()) {
        ul("list-inline row no-gutters justify-content-start") {
            units.filter { filterCallback(it) }.forEach { unit ->
                li("col-auto") {
                    unitUi(unit, { eventHandler.click(unit) })
                }
            }
        }
    }
}

fun RBuilder.unitList(units: List<UnitInfo>, filterCallback: (UnitInfo) -> Boolean, clickCallback: (UnitInfo) -> Unit, selectedUnit: UnitInfo? = null) {
    val eventHandler = object : UnitListEventHandler {
        override fun click(unit: UnitInfo) {
            clickCallback(unit)
        }
    }

    if (units.isNotEmpty()) {
        ul("list-inline row no-gutters justify-content-start") {
            units.filter { filterCallback(it) }.forEach { unit ->
                li("col-auto") {
                    val addClass = if (selectedUnit != null && selectedUnit == unit) "selected" else ""
                    unitUi(unit.def, { eventHandler.click(unit) }, addClass)
                }
            }
        }
    }
}