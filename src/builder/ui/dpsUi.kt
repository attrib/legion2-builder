package builder.ui

import app.format
import builder.data.Resistance
import kotlinext.js.invoke
import kotlinx.html.title
import react.RBuilder
import react.dom.div
import react.dom.img

fun RBuilder.dpsUi(resistence: Resistance) {
    div("tooltip-data") {
        for ((type, value) in resistence.dps) {
            div {
                img(alt = type.toString(), src = "Icons/" + type.toString() + ".png") { attrs.title = type.toString() }
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
                img(alt = type.toString(), src = "Icons/" + type.toString() + ".png") { attrs.title = type.toString() }
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