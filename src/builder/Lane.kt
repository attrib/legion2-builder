package builder

import builder.data.Unit

class Lane {

    val fighters: MutableList<Unit> = mutableListOf()
    val mercenaries: MutableList<Unit> = mutableListOf()

    val totalHp get() = fighters.sumBy { it.hp }
    val totalDps get() = fighters.sumByDouble { it.dps ?: 0.0 }


}