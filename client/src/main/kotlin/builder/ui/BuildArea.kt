package builder.ui

import builder.data.UnitSelection
import d3.d3_wrapper
import ltd2.Build
import ltd2.UnitState
import kotlinx.html.id
import ltd2.UnitDef
import react.RBuilder
import react.dom.a
import react.dom.div
import kotlin.browser.document

interface BuildAreaEventHandler {
    fun selectUnit(unit: UnitState)
    fun addFighter(unit: UnitDef, x: Int, y: Int)
}

fun RBuilder.buildArea(build: Build, selectedUnit: UnitSelection, eventHandler: BuildAreaEventHandler) {
    div {
        attrs.id = "build-area"
        d3_wrapper.render("build-area", selectedUnit, build.getFighters().removeWorkersNotLevel(build.currentLevel).toTypedArray(),
                { unitDef: UnitDef, x: Int, y: Int ->
                    eventHandler.addFighter(unitDef, x, y)
                },
                { unitState: UnitState ->
                    eventHandler.selectUnit(unitState)
                })
    }
    d3_wrapper.drawInlineSVG({ url ->
        val element = document.getElementById("download-link")
        if (element !== null) {
            element.setAttribute("href", url)
            element.setAttribute("download", "Wave" + (build.currentLevel + 1) + ".png")
            element.innerHTML = "Download as Image"
        }
    })
    a {
        attrs.id = "download-link"
    }
}