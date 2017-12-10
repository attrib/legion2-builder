package builder.data

import kotlin.js.Json

class LegionTD2(json: Json) {
    val units: Map<String, Unit> by jo(json, { Unit(it) })
    val legions: Map<String, Legion> by jo(json, { Legion(it) })
    val globals: Map<String, Global> by jo(json, { Global(it) })
}