package builder.ui

import index.LegionTD2Builder
import kotlinx.html.title
import react.RBuilder
import react.dom.img

fun RBuilder.IconImg(iconPath: String, title: String, width: Int? = null, height: Int? = null) {
    val path = LegionTD2Builder.iconsPath + iconPath.replace("Splashes/", "").replace("Icons/", "")
    img(title, path) {
        attrs.title = title
        if (width != null) {
            attrs.width = width.toString() + "px"
        }
        if (height != null) {
            attrs.height = height.toString() + "px"
        }
    }
}