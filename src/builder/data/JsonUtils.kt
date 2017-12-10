package builder.data

import kotlinext.js.Object
import kotlin.js.Json
import kotlin.reflect.KProperty


class JsonObjectProperty<T>(val json: Json, val name: String, val factory: (Json) -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Map<String, T> {
        val value = json[name] as Json
        val result = mutableMapOf<String, T>()
        for (k in Object.keys(value)) {
            result[k] = factory(value[k] as Json)
        }
        return result
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Map<String, T>) {
        println("$value has been assigned to '${property.name} in $thisRef.'")
    }
}

class JsonProperty<T>(val json: Json, val name: String, val factory: (Any?) -> T) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val value = json[name]
        if (value != null && value != undefined) {
            return factory(value)
        } else {
            throw IllegalArgumentException("$name should not be undefined!")
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("$value has been assigned to '${property.name} in $thisRef.'")
    }
}

class JsonPropertyLoader<T>(val json: Json, val name: String? = null, val factory: (Any?) -> T) {
    operator fun provideDelegate(
            thisRef: Any,
            prop: KProperty<*>
    ): JsonProperty<T> {
        // create delegate
        if (name != null) {
            return JsonProperty(json, name, factory)
        }
        return JsonProperty(json, prop.name.toLowerCase(), factory)
    }
}

class JsonObjectPropertyLoader<T>(val json: Json, val name: String? = null, val factory: (Json) -> T) {
    operator fun provideDelegate(
            thisRef: Any,
            prop: KProperty<*>
    ): JsonObjectProperty<T> {
        // create delegate
        if (name != null) {
            return JsonObjectProperty(json, name, factory)
        }
        return JsonObjectProperty(json, prop.name.toLowerCase(), factory)
    }
}

fun jpInt(json: Json, name: String? = null) =
        JsonPropertyLoader(json, name, { (it as String).toIntOrNull() ?: 0 })

fun jpDouble(json: Json, name: String? = null) =
        JsonPropertyLoader(json, name, { (it as String).toDoubleOrNull() ?: 0.0 })

fun <T> jp(json: Json, factory: (Any?) -> T = { it as T }, name: String? = null): JsonPropertyLoader<T> {
    return JsonPropertyLoader(json, name, factory)
}

fun <T> jo(json: Json, factory: (Json) -> T, name: String? = null): JsonObjectPropertyLoader<T> {
    return JsonObjectPropertyLoader(json, name, factory)
}