package index

import app.*
import kotlinext.js.*
import react.dom.*
import kotlin.browser.*

@JsModule("lz-string")
external object LZString {
    fun compressToBase64(s:String) : String
    fun decompressFromBase64(s:String) : String
}

external object LegionTD2Builder {
    val containerId: String
    val gaId: String?
}


fun main(args: Array<String>) {
    val container = document.getElementById(LegionTD2Builder.containerId)
    if( container!=null ) {
        require("index/bootstrap.min.css")
        require("index/index.css")
        require("app/App.css")
        require("index/loading.css")
        require("builder/ui/tab/BuildOrder.css")

        render(container) {
            app()
        }
    }
}
