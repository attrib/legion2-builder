package builder.data

class Legion(
        val name: String,
        val iconpath: String,
        val playable: String
) {

    val creatures: MutableList<Unit> = mutableListOf()

    fun isPlayable(): Boolean {
        return playable == "Playable"
    }

}