package ltd2

import kotlin.test.Test
import kotlin.test.assertEquals

class PermaLinkTest {
    @Test
    fun testPermaLink() {
        println("Hello from PermaLinkTestCommon")
        val orig = Build(Lane(mutableListOf(UnitState(LegionData.unitsMap["worker_unit_id"]!!))))
        val data = PermaLinkV1.toPermaLinkCode(orig)
        val new = PermaLinkV1.fromPermaLinkCode(data)
        assertEquals(orig.lane.units[0].def.id, new.lane.units[0].def.id)
    }
}