package builder.data

import ltd2.Research
import ltd2.ResearchDef
import ltd2.UnitDef
import ltd2.UnitState

class UnitSelection {

    private var selected: Any? = null

    fun clearSelection() {
        selected = null
    }

    fun select(unit: Any) {
        if (unit == selected) {
            clearSelection()
            return
        }
        clearSelection()
        selected = unit
    }

    fun isBuiltUnit(): Boolean {
        return selected is UnitState
    }

    fun isNewUnit(): Boolean {
        return selected is UnitDef
    }

    fun isBuiltResearch(): Boolean {
        return selected is Research
    }

    fun isNewResearch(): Boolean {
        return selected is ResearchDef
    }

    fun getBuiltUnit(): UnitState {
        return selected!! as UnitState
    }

    fun getNewUnit() : UnitDef {
        return selected!! as UnitDef
    }

    fun isSelected(compare: Any): Boolean {
        return selected !== null && compare === selected
    }

}
