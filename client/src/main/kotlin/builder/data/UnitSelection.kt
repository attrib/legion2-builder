package builder.data

import ltd2.UnitDef
import ltd2.UnitState

class UnitSelection {

    private var builtUnit: UnitState? = null
    private var newUnit: UnitDef? = null

    fun clearSelection() {
        builtUnit = null
        newUnit = null
    }

    fun select(unit: UnitState) {
        if (unit == builtUnit) {
            clearSelection()
            return
        }
        clearSelection()
        builtUnit = unit
    }

    fun select(unit: UnitDef) {
        if (unit == newUnit) {
            clearSelection()
            return
        }
        clearSelection()
        newUnit = unit
    }

    fun isBuiltUnit(): Boolean {
        return builtUnit !== null
    }

    fun isNewUnit(): Boolean {
        return newUnit !== null
    }

    fun getBuiltUnit(): UnitState {
        return builtUnit!!
    }

    fun getNewUnit() : UnitDef {
        return newUnit!!
    }

    fun isSelected(unit: UnitDef): Boolean {
        return newUnit !== null && unit === newUnit
    }

    fun isSelected(unit: UnitState): Boolean {
        return builtUnit !== null && unit === builtUnit
    }

}
