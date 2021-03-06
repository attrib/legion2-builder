package index

import app.app
import kotlinext.js.*
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

@JsModule("lz-string")
external object LZString {
    fun compressToBase64(s:String) : String
    fun decompressFromBase64(s:String) : String
}

external object LegionTD2Builder {
    val containerId: String
    val gaId: String?
    var basePath: String?
    var iconsPath: String?
    var queryString: String?
}


fun main(args: Array<String>) {
    if (jsTypeOf(LegionTD2Builder) != "object") {
        console.error("LegionTD2Builder is not defined.")
        return
    }
    if (jsTypeOf(LegionTD2Builder.containerId) != "string") {
        console.error("LegionTD2Builder.containerId is not defined.")
        return
    }
    if (LegionTD2Builder.basePath == null) {
        LegionTD2Builder.basePath = window.location.pathname
    }
    if (LegionTD2Builder.basePath!!.endsWith("/")) {
        LegionTD2Builder.basePath = LegionTD2Builder.basePath!!.substring(-1)
    }
    if (LegionTD2Builder.queryString == null) {
        LegionTD2Builder.queryString = "?" + window.location.search.substring(1).split("&").filter { !it.startsWith("b=") }.joinToString("&")
    }
    if (LegionTD2Builder.queryString!!.length > 1) {
        LegionTD2Builder.queryString += "&"
    }
    if (LegionTD2Builder.iconsPath == null) {
        LegionTD2Builder.iconsPath = LegionTD2Builder.basePath!!.substring(0, window.location.pathname.lastIndexOf("/")) + "/Icons/"
    }
    if (!LegionTD2Builder.iconsPath!!.endsWith("/")) {
        LegionTD2Builder.iconsPath += "/"
    }
    val container = document.getElementById(LegionTD2Builder.containerId)
    if (container != null) {
        require("index/bootstrap.min.css")
        require("index/index.css")
        require("app/App.css")
        require("index/loading.css")
        require("builder/ui/tab/BuildOrder.css")

        render(container) {
            app()
        }
    }
    else {
        console.error("Container with ID ${LegionTD2Builder.containerId} not found.")
    }
}
