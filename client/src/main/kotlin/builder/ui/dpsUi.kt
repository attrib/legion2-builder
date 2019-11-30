package builder.ui

import app.format
import ltd2.Resistance
import kotlinext.js.invoke
import kotlinx.html.title
import react.RBuilder
import react.dom.div
import react.dom.img

fun RBuilder.dpsUi(resistence: Resistance) {
    div("tooltip-data") {
        for ((type, value) in resistence.dps) {
            div {
                IconImg("Icons/$type.png", type.name)
                +type.toString()
                +": "
                +value.format(0)
                val mod = resistence.getModAttack(type)
                if (mod > 0.0) {
                    +" ("
                    +(value * mod).format(2)
                    +")"
                }
            }
        }
    }
}

fun RBuilder.hpUi(resistence: Resistance) {
    div("tooltip-data") {
        for ((type, value) in resistence.hps) {
            div {
                IconImg("Icons/$type.png", type.name)
                +type.toString()
                +": "
                +value.toString()
                val mod = resistence.getModDefense(type)
                if (mod > 0.0) {
                    +" ("
                    +(value * mod).format(2)
                    +")"
                }
            }
        }
    }
}