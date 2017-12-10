package builder.data

data class LegionTD2(
        val units: Map<String, Unit>,
        val legions: Map<String, Legion>,
        val globals: Map<String, Global>
)