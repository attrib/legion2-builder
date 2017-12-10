package builder.data

import kotlin.js.Json

class Legion(json: Json) {
    val name: String by jp(json)
    val iconpath: String by jp(json)
    val playable: String by jp(json)

    val fighters: MutableMap<String, Unit> = mutableMapOf()

    fun isPlayable(): Boolean {
        return playable == "Playable"
    }

}