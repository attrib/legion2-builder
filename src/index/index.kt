package index

import app.*
import builder.DSFactory
import kotlinext.js.*
import org.khronos.webgl.ArrayBuffer
import react.dom.*
import kotlin.browser.*

@JsModule("lz-string")
external object LZString {
    fun compressToBase64(s:String) : String
    fun decompressFromBase64(s:String) : String
}


fun main(args: Array<String>) {
    require("src/index/bootstrap.min.css")
    require("src/index/index.css")
    require("src/app/App.css")
    require("src/index/loading.css")
    require("src/builder/ui/BuildOrder.css")

    render(document.getElementById("root")) {
        app()
    }
}
