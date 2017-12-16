package parser

import builder.Build
import builder.LegionData


data class WaveTimeEntry(
        val gold: Double,
        val mythium: Double,
        val value: Double,
        val foodCap: Int,
        val workers: Int
)

data class PlayerWave(
        val start: WaveTimeEntry,
        val end: WaveTimeEntry,
        val remainingHitpoints: Double,
        val buildings: Snapshot
)

data class Wave(val number: Int, val start: Int, val end: Int) {
    val playerWaves = mutableMapOf<String, PlayerWave>()
    var unitsSent: Snapshot? = null
}

data class GameResult(val players: List<String>, val waves: List<Wave>)

data class Snapshot(val entities: List<EntitySnapshot>) {
    fun workers(): Snapshot = Snapshot(entities.filter { it.attributes["Type"] == "worker_unit_id" || it.attributes["Type"] == "worker_x2_unit_id" })
    fun noWorkers(): Snapshot = Snapshot(entities.filter { it.attributes["Type"] != "worker_unit_id" && it.attributes["Type"] != "worker_x2_unit_id" })
    fun byOwners(): Map<String, Snapshot> = entities.groupBy { it.attributes["Owner"] ?: "" }.mapValues { Snapshot(it.value) }
    fun hasNotFlag(flag: String): Snapshot {
        return Snapshot(entities.filter { !(it.attributes["Flags"]?.contains(flag) ?: false) })
    }

    fun hasFlag(flag: String): Snapshot {
        return Snapshot(entities.filter { it.attributes["Flags"]?.contains(flag) ?: false })
    }
}

data class EntitySnapshot(val attributes: Map<String, String>, val entity: Entity)
enum class Currency {
    GOLD,
    MYTHIUM
}

data class Payment(val playerId: Int, val time: Int, val from: Int, val to: Int, val currency: Currency, val entity: Entity?)
data class ReplayResult(val playerBuilds: Map<String, Build>)
class Replay(val logFile: LogFile) {
    private var waves = emptyList<Wave>()
    fun fullBuild(): Map<Int, Snapshot> {
        val map = mutableMapOf<Int, Snapshot>()
        logFile.timeLine.forEach { time ->
            val sn = buildSnapshot(time.time)
            println("$time ${sn.entities.size}")
//            map[time.time] = sn
        }
        return map
    }

    fun findPayments(name: String): List<Payment> {
        var payments = emptyList<Payment>()
        val player = logFile.players.values.first { it.attr("Name", 10000) == name }
        var last = 0
        player.attr("Mythium")?.forEach { p ->
            val value = p.value.toInt()
            if (value < last) {
                val before = buildSnapshot(p.time, { it.attr("Owner", p.time) == "12" })
                val after = buildSnapshot(p.time + 400, { it.attr("Owner", p.time) == "12" })
                val units = after.entities.map { it.entity }.toMutableList()
                before.entities.forEach {
                    units.remove(it.entity)
                }
                if (units.size == 0) {
                    payments += Payment(0, p.time, last, value, Currency.MYTHIUM, null)
                } else if (units.size == 1) {
                    payments += Payment(0, p.time, last, value, Currency.MYTHIUM, units[0])
                } else {
                    println("MULTIPLE FOUND")
                }
            }
            last = value
        }
        return payments
    }

    fun build(): GameResult {
        var currentWaveStart = 0
        logFile.timeLine.forEach { time ->
            val units = time.affectedUnits.map { logFile.units[it] }.filterNotNull()
            units.forEach { unit ->
                val type = unit.attr("Type", time.time)
                when (type) {
                    "wave_in_progress_unit_id" -> {
                        val active = unit.attr("Active", time.time)!! == "True"
                        if (active) {
                            currentWaveStart = time.time
                        } else {
                            if (currentWaveStart != 0) {
                                waves += Wave(waves.size + 1, currentWaveStart, time.time)
                                currentWaveStart = 0
                            }
                        }
                    }
                }
            }
        }

        val sendUnit = if (logFile.players[11]!!.attr("Gold", 100000) != "0") {
            11
        } else if (logFile.players[12]!!.attr("Gold", 100000) != "0") {
            12
        } else {
            throw IllegalArgumentException()
        }

        fun format(list: List<EntitySnapshot>?) = list?.map { it.attributes["Type"] }?.joinToString(", ") ?: ""

        var players = emptyList<String>()
        (1..8).forEach { playerId ->
            val entity = logFile.players[playerId]!!
            val gold = entity.attr("Gold", 200000)?.toInt() ?: 0
            if (gold > 0) {
                players += entity.attr("Name", 200000)!!
            }
        }
        if (players.size != 4) {
            throw IllegalArgumentException("Wrong number of players!")
        }

        waves.forEach { wave ->
            val atStart = buildSnapshot(wave.start - 1)
            val atEnd = buildSnapshot(wave.end)
            val startByOwners = atStart.byOwners()
            val endByOwners = atEnd.byOwners()
            wave.unitsSent = endByOwners[sendUnit.toString()]
            (1..8).forEach { playerId ->

                val start = startByOwners[playerId.toString()]
                val end = endByOwners[playerId.toString()]
                if (end != null) {
                    val name = logFile.players[playerId]!!.attr("Name", wave.start)!!
                    val remainingHp = start?.hasNotFlag("Building")?.entities?.map { it.attributes["Hp"]!!.toDouble() }?.sum() ?: 0.0
                    val endBuildings = end.hasFlag("Building")
                    val valueStart = buildValue(start?.hasNotFlag("Building")?.noWorkers())
                    val valueEnd = buildValue(endBuildings.noWorkers())
                    val workersStart = start?.workers()?.entities?.sumBy { if (it.attributes["Type"] == "worker_unit_id") 1 else 2 } ?: 0
                    val workersEnd = end.workers().entities.sumBy { if (it.attributes["Type"] == "worker_unit_id") 1 else 2 }
                    wave.playerWaves[name] = PlayerWave(
                            WaveTimeEntry(
                                    logFile.players[playerId]!!.attr("Gold", wave.start - 1)!!.toDouble(),
                                    logFile.players[playerId]!!.attr("Mythium", wave.start - 1)!!.toDouble(),
                                    valueStart,
                                    logFile.players[playerId]!!.attr("Food Cap", wave.start - 1)!!.toInt(),
                                    workersStart
                            ),
                            WaveTimeEntry(
                                    logFile.players[playerId]!!.attr("Gold", wave.end)!!.toDouble(),
                                    logFile.players[playerId]!!.attr("Mythium", wave.end)!!.toDouble(),
                                    valueEnd,
                                    logFile.players[playerId]!!.attr("Food Cap", wave.end)!!.toInt(),
                                    workersEnd), remainingHp, endBuildings)
                }

            }
        }
        return GameResult(players, waves)
    }

    private fun buildValue(snapshot: Snapshot?): Double {
        if (snapshot == null) {
            return 0.0
        }
        return snapshot.entities.map {
            val hp = it.attributes["Hp"]!!.toDouble()
            val maxHp = it.attributes["Max Hp"]!!.toDouble()
            val unitDef = LegionData.unitsMap[it.attributes["Type"]!!]!!
            val value = unitDef.totalValue
            value * hp / maxHp
        }.sum()
    }

    private fun buildSnapshot(time: Int, filter: (Entity) -> Boolean = { true }): Snapshot {
        val entitySnapshots = logFile.units.values.filter {
            it.attr("Active", time) == "True"
        }.filter(filter).filter {
            val type = it.attr("Type", time)!!
            !type.contains("_builder_")
                    && type != "wave_in_progress_unit_id"
                    && type != "town_unit_id"
                    && type != "barracks_unit_id"
        }.map { entity ->
            val map = mutableMapOf<String, String>()
            entity.attributes.forEach { attr ->
                val value = attr.at(time)?.value
                if (value != null) {
                    map[attr.id] = value
                }
            }
            EntitySnapshot(map, entity)
        }
        return Snapshot(entitySnapshots)
    }
}

inline fun <K, V, R> Map<out K, V>.map(callback: (K, V) -> R) = map { (k, v) -> callback(k, v) }

fun replay(logdata: LogFile): ReplayResult {
    val gameData = LegionData
    val r = Replay(logdata)
    val game = r.build()
    var last = mutableListOf<String>()

    val playerBuilds = game.players.associateBy({ it }, { player ->
        val build = Build()
        game.waves.forEach { wave ->
            val playerWave = wave.playerWaves[player]!!
            val current = playerWave.buildings.entities.map { it.attributes["Type"]!! } + (1..playerWave.end.workers).map { "worker_unit_id" }
            val diffAdded = current.toMutableList()
            val diffRemoved = last.toMutableList()
            last.forEach {
                diffAdded.remove(it)
            }
            current.forEach {
                diffRemoved.remove(it)
            }
            last = current.toMutableList()

            val value = current.sumBy { gameData.unitsMap[it]!!.totalValue }


            var text = emptyList<String>()
            var gold = 0
            diffAdded.forEach { added ->
                val unitAdded = gameData.unitsMap[added]!!
                if (unitAdded.upgradesFrom != null) {
                    val unitUpgraded = gameData.unitsMap[unitAdded.upgradesFrom]!!
                    if (diffRemoved.contains(unitUpgraded.id)) {
                        gold += unitAdded.goldCost
                        text += "${unitUpgraded.name} -> ${unitAdded.name}"
                        build.addFighter(unitAdded)
                    } else {
                        gold += unitUpgraded.goldCost
                        gold += unitAdded.goldCost
                        text += "${unitAdded.name} (${unitUpgraded.name})*"
                        build.addFighter(unitAdded)
                    }
                } else {
                    gold += unitAdded.goldCost
                    text += unitAdded.name

                    build.addFighter(unitAdded)
                }
            }
            build.levelIncrease()
        }
        build
    })

    return ReplayResult(playerBuilds)
}