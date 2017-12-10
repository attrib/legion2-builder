package builder.data

class Legion(
        val name: String,
        val iconpath: String,
        val playable: Playable
) {

    val creatures: MutableList<Unit> = mutableListOf()

}