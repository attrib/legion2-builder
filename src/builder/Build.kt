package builder

import builder.data.Legion

class Build {

    var legionId: String = ""
    var legion: Legion? = null
    val lane = Lane()
    var currentLevel = 1

    val costs get() = 0
    val available get() = 250 - costs
}