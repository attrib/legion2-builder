package builder.data

class Legion(
        val name: String,
        val iconpath: String,
        val playable: String
) {

    val fighters: MutableMap<String, Unit> = mutableMapOf()

    fun isPlayable(): Boolean {
        return playable == "Playable"
    }

}