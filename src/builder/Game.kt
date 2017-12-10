package builder

import builder.data.*
import kotlinext.js.Object
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Json

interface GameEventHandler {
    fun loaded()
}

data class test(val a:String, val b:List<String>, val c: Map<String, String>)

class Game(gameEventHandler: GameEventHandler) {

    val legions: MutableMap<String, Legion> = mutableMapOf()
    val mercenaries: MutableList<Unit> = mutableListOf()
    var data: LegionTD2 = LegionTD2(mutableMapOf(), mutableMapOf(), mutableMapOf())

    init {
        val request = XMLHttpRequest()
        request.overrideMimeType("application/json")
        request.open("GET", "LTD2_Data.json", true)
        request.onreadystatechange = {
            if (request.readyState == XMLHttpRequest.DONE && request.status == 200.toShort())  {
                val data = JSON.parse<Json>(request.responseText)
                val legionsData = data["legions"] as Json
                for (k in Object.keys(legionsData!!)) {
                    val legion = legionsData[k] as Json
                    legions.put(k, Legion(legion["name"] as String, legion["iconpath"] as String, legion["playable"] as String))
                }
                console.log(data)
                gameEventHandler.loaded()
            }
        }
        request.send()
    }

}