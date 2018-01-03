package d3

import builder.data.UnitSelection
import ltd2.UnitDef
import ltd2.UnitState

@JsModule("d3/d3-wrapper.js")
external object d3_wrapper {
    fun init()
    fun render(id: String,
               selectedBuildUnit: UnitSelection,
               units: Array<UnitState>,
               buildUnitCallback: (UnitDef, Int, Int) -> Unit,
               selectUnitCallback: (UnitState) -> Unit)
    fun drawInlineSVG(callback: (String) -> Unit)
}