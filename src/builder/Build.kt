package builder

import builder.data.Legion

class Build {

    var legionId: String = ""
    var legion: Legion? = null
    val lane = Lane()
    var currentLevel = 1

    val costs get() = lane.fighters.sumBy { it.totalvalue ?: 0 }
    val foodCosts get() = lane.fighters.sumBy { it.totalfood ?: 0 }
    val available get() = 250 - costs
    val income get() = lane.mercenaries.sumBy { it.incomebonus ?: 0 }
}