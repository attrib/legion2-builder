package builder.ui

import app.format
import builder.data.Resistance
import kotlinx.html.title
import react.RBuilder
import react.dom.div
import react.dom.img

fun RBuilder.dpsUi(resistence: Resistance) {
    div("dps-info") {
        for ((type, value) in resistence.dps) {
            div {
                img(alt = type.toString(), src = "Icons/" + type.toString() + ".png") { attrs.title = type.toString() }
                +type.toString()
                +": "
                +value.format(0)
            }
        }
    }
}

fun RBuilder.hpUi(resistence: Resistance) {
    div("hp-info") {
        for ((type, value) in resistence.hps) {
            div {
                img(alt = type.toString(), src = "Icons/" + type.toString() + ".png") { attrs.title = type.toString() }
                +type.toString()
                +": "
                +value.toString()
            }
        }
    }
}