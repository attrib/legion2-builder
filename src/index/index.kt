package index

import app.*
import kotlinext.js.*
import react.dom.*
import kotlin.browser.*

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
