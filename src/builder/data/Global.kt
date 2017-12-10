package builder.data

import kotlin.js.Json

class Global(json: Json) {
    val name: String by jp(json)
    val attackchartpierce: String by jp(json)
    val attackchartnormal: String by jp(json)
    val attackchartmagic: String by jp(json)
    val attackchartsiege: String by jp(json)
    val attackchartchaos: String by jp(json)
}