package builder.data

import kotlin.js.Json

class Wave(json: Json) {

    val name: String by jp(json)
    val amount: Int by jpInt(json)
    val amount2: Int by jpInt(json)
    val levelnum: Int by jpInt(json)
    val totalreward: Int by jpInt(json)
    val preparetime: Int by jpInt(json)
    val iconpath: String by jp(json)
    val unit_id: String by jp(json)
    val spellunit2_id: String by jp(json)
    val creatures: MutableList<Unit> = mutableListOf()

    val totalHp get() = creatures.sumBy { it.hp * it.amount }
    val totalDps get() = creatures.sumByDouble { it.dps * it.amount }
}