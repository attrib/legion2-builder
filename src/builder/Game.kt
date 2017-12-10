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

    val legions: MutableList<Legion> = mutableListOf()
    val mercenaries: MutableList<Unit> = mutableListOf()
    var data: LegionTD2 = LegionTD2(mutableMapOf(), mutableMapOf(), mutableMapOf())

    init {
        for (legion in LegionEnum.values()) {
            val name = when(legion) {
                LegionEnum.ELEMENT -> "Element"
                LegionEnum.FORSAKEN -> "Forsaken"
                LegionEnum.GROVE -> "Grove"
                LegionEnum.MECH -> "Mech"
            }
            legions.add(legion.ordinal, Legion(name, "", Playable.IsPlayable))
        }

        val request = XMLHttpRequest()
        request.overrideMimeType("application/json")
        request.open("GET", "LTD2_Data.json", true)
        request.onreadystatechange = {
            if (request.readyState == XMLHttpRequest.DONE && request.status == 200.toShort())  {
                data = JSON.parse(request.responseText)
//                data = JSON.parse(request.responseText, {key: String, value: Any? ->
//                    return@parse when(key) {
//                        "units" -> {
//                            val obj: Json = value as Json
//                            val mapping = mutableMapOf<String, Unit>()
//                            for (k in Object.keys(obj)) {
//                                mapping.put(k, obj[k] as Unit)
//                            }
//                            mapping
//                        }
//                        "legions" -> {
//                            val obj: Json = value as Json
//                            val mapping = mutableMapOf<String, Legion>()
//                            for (k in Object.keys(obj)) {
//                                mapping.put(k, obj[k] as Legion)
//                            }
//                            mapping
//                        }
//                        "globals" -> {
//                            val obj: Json = value as Json
//                            val mapping = mutableMapOf<String, Global>()
//                            for (k in Object.keys(obj)) {
//                                mapping.put(k, obj[k] as Global)
//                            }
//                            mapping
//                        }
//                        else -> value
//                    }
//                })
                console.log(data)
                //gameEventHandler.loaded()
            }
        }
        request.send()
    }

    fun getLegion(legionEnum: LegionEnum): Legion {
        return legions[legionEnum.ordinal]
    }


}