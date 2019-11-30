package builder.ui.header

import app.format
import builder.ui.dpsUi
import builder.ui.hpUi
import kotlinx.html.js.onClickFunction
import ltd2.Resistance
import ltd2.UnitDef
import react.RBuilder
import react.dom.button
import react.dom.div
import react.dom.h3

interface LevelInterface {
    fun decrease()
    fun increase()
}

fun RBuilder.waveInfo(waveDef: List<UnitDef>, currentLevel: Int, eventHandler: LevelInterface) {
    h3 { +"Wave info" }
    div {
        +"Level: "
        +(currentLevel + 1).toString()
    }
    div("tooltip-parent") {
        +"Total HP: "
        +waveDef.sumBy { it.hitpoints }.toString()
        hpUi(Resistance(waveDef, null))
    }
    div("tooltip-parent") {
        +"Total DPS: "
        +waveDef.sumByDouble { it.dmgBase * it.attackSpeed }.format(2)
        dpsUi(Resistance(waveDef, null))
    }
    div("btn-group btn-group-sm") {
        button(classes = "btn btn-secondary btn-sm col") {
            +"-"
            attrs.onClickFunction = {
                eventHandler.decrease()
            }
        }
        button(classes = "btn btn-secondary btn-sm col") {
            +"+"

            attrs.onClickFunction = {
                eventHandler.increase()
            }
        }
    }

}