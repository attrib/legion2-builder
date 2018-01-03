package ltd2

import builder.PermaLinkV1JS
import kotlin.test.Test
import kotlin.test.assertEquals

class PermaLinkJsTest {
    @Test
    fun testPermaLink() {
        println("Hello from PermaLinkTestJS2")

        val orig = Build(Lane(mutableListOf(UnitState(LegionData.unitsMap["worker_unit_id"]!!))))
        val data = PermaLinkV1JS.toPermaLinkCode(orig)
        val new = PermaLinkV1JS.fromPermaLinkCode(data)

        assertEquals(orig.lane.units[0].def.id, new.lane.units[0].def.id)
    }
}