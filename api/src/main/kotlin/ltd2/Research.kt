package ltd2

import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerialClassDesc
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.SerialClassDescImpl

object ResearchSerializer : KSerializer<Research> {
    override fun save(output: KOutput, obj: Research) {
        obj.apply {
            output.writeStringValue(def.id)
            output.writeByteValue((buildLevel ?: -1).toByte())
            output.writeByteValue((upgradeLevel).toByte())
        }
    }

    override fun load(input: KInput): Research {
        val index = input.readStringValue()
        val research = Research(LegionData.researchMap[index]!!)
        research.buildLevel = intOrNull(input.readByteValue())
        research.upgradeLevel = input.readByteValue().toInt()
        return research
    }

    override val serialClassDesc: KSerialClassDesc = SerialClassDescImpl("ltd2.Research")

}

class Research(val def: ResearchDef) {
    companion object {
        const val WORKER_ID = "worker_unit_id"
        const val SUPPLY_RESEARCH_ID = "upgrade_supply_research_id"
    }

    var buildLevel: Int? = null
    var upgradeLevel: Int = 0
}