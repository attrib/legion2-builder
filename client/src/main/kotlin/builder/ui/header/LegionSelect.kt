package builder.ui.header

import builder.Build
import ltd2.Legion
import ltd2.LegionData
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get
import org.w3c.files.Blob
import org.w3c.files.FileReader
import org.w3c.files.get
import parser.ExtractBuilds
import parser.LogParser
import parser.ReplayResult
import react.RBuilder
import react.dom.*

interface LegionSelectEventHandler {
    fun changeLegion(legion: Legion)
    fun reset()
    fun replayFileSelected()
    fun replayFileLoaded(replayResult: ReplayResult)
    fun replayPlayerSeleceted(player: String)
}

fun RBuilder.legionSelect(build: Build, replayResult: ReplayResult?, selectedPlayer: String?, eventHandler: LegionSelectEventHandler) {
    h3 { +"Legion" }
    div("btn-group btn-group-sm") {
        LegionData.legionsMap.forEach { (legionId, legion) ->
            if (!legion.playable) {
                return@forEach
            }
            label("btn btn-secondary btn-sm col") {
                if (build.legionId == legionId) {
                    attrs.classes += "active"
                }
                input(type = InputType.radio, name = "legion", classes = "d-none") {
                    attrs.onChangeFunction = {
                        val target = it.target as HTMLInputElement
                        eventHandler.changeLegion(LegionData.legionsMap[target.value]!!)
                    }
                    attrs.value = legionId
                    if (build.legionId == legionId) {
                        attrs.checked = true
                    }
                }
                img(alt = legion.name, src = legion.iconPath) {
                    attrs.title = legion.name
                    attrs.width = "32px"
                }
            }
        }
    }
    div("btn-group") {
        button(classes = "btn btn-secondary col") {
            +"reset"
            attrs.onClickFunction = {
                eventHandler.reset()
            }
        }

        a(href = "?b=${build.toPermaLinkCode()}", classes = "btn btn-secondary col") {
            +"Permalink"
        }
    }
    div("btn-group") {
        label(classes = "col replay-upload btn btn-secondary col") {
            input(InputType.file, classes = "d-none") {
                attrs.accept = "text/plain"
                attrs.onChangeFunction = {
                    val target = it.target as HTMLInputElement
                    val fr = FileReader()
                    fr.onload = {
                        val text = (it.target!! as FileReader).result as String
                        eventHandler.replayFileLoaded(ExtractBuilds.extactBuilds(LogParser(text).parse()))
                    }
                    fr.readAsText(target.files!![0] as Blob)
                    eventHandler.replayFileSelected()
                }
            }
            +"Select Replay"
        }
        if (replayResult !== null) {
            select(classes = "btn btn-secondary col") {
                option {
                    +""
                }
                replayResult.playerBuilds.keys.forEach { player ->
                    option {
                        attrs.selected = player == selectedPlayer ?: ""
                        +player
                    }
                }
                attrs.onChangeFunction = {
                    val target = it.target as HTMLSelectElement
                    val player = target.selectedOptions[0]
                    eventHandler.replayPlayerSeleceted(player?.innerHTML!!)
                }
            }
        }
    }
}