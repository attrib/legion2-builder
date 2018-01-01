package ltd2

import java.io.PrintStream
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

fun type2String(type:KType) : String {
    val stype = type.toString().replace("kotlin.","").replace("ltd2.", "")
    if( stype=="DecimalArray" ) {
        return "List<Double>"
    }
    return stype
}

fun value2String(type:KType, value:Any?) : String {
    var stype = type2String(type)
    var optional = false
    if( stype.endsWith("?")) {
        stype = stype.substring(0, stype.length-1)
        optional = true
    }
    if( value==null && optional) {
        return "null"
    }
    when(stype) {
        "String"->return if( value!=null ) "\"$value\"" else ""
        "AttackMode"->return if( value!=null ) "AttackMode.$value" else "AttackMode.Illegal"
        "ArmorType"->return if( value!=null ) "ArmorType.$value" else "ArmorType.Illegal"
        "AttackType"->return if( value!=null ) "AttackType.$value" else "AttackType.Illegal"
        "UnitClass"->return if( value!=null ) "UnitClass.$value" else "UnitClass.Illegal"
        "Legion"->return if( value!=null ) "Legion.$value" else "Legion.Illegal"
        "List<Double>"->return "listOf(${(value as DecimalArray).list.joinToString(",")})"
    }
    return value.toString()
}
fun writeClassDef(cls: KClass<*>) {
    println("data class ${cls.simpleName}(")
    println(cls.memberProperties.map{
        "\tval ${it.name} : ${type2String(it.returnType)}"
    }.joinToString(",\n"))
    println(")")
}
fun <T> writeEnum(name:String, values:Array<T>) {
    println("enum class $name {")
    println(values.map {
        "\t$it"
    }.joinToString(",\n"))
    println("}")
}
fun main(args: Array<String>) {
    val data = loadData("D:\\Spiele\\Steam\\steamapps\\common\\Legion TD 2")
    (System::setOut)(PrintStream("api/src/main/kotlin/ltd2/ltd2.kt"))
    println("package ltd2")
    println("")
    writeEnum("ArmorType", ArmorType.values())
    writeEnum("AttackType", AttackType.values())
    writeEnum("AttackMode", AttackMode.values())
    writeEnum("UnitClass", UnitClass.values())
    writeClassDef(Legion::class)
    writeClassDef(UnitDef::class)
    writeClassDef(ResearchDef::class)
    writeClassDef(WaveDef::class)
    writeClassDef(BuffDef::class)
    writeClassDef(Global::class)
    println("object LegionData {")
    println("\tval buffs = listOf(")
    println(data.buffs.buffs.map { buffDef->
        "\t\tBuffDef(${BuffDef::class.memberProperties.map{ value2String(it.returnType, it.get(buffDef))}.joinToString(", ")})"
    }.joinToString(",\n"))
    println("\t)")

    println("\tval legions = listOf(")
    println(data.legions.legions.map { legion->
        "\t\tLegion(${Legion::class.memberProperties.map{ value2String(it.returnType, it.get(legion))}.joinToString(", ")})"
    }.joinToString(",\n"))
    println("\t)")
    println("\tval legionsMap = legions.associateBy { it.id }\n")
    println("\tval units = listOf(")
    println(data.unitDefs.unitDefs.map {unitDef->
        "\t\tUnitDef(${UnitDef::class.memberProperties.map { value2String(it.returnType, it.get(unitDef)) }.joinToString(", ")})"
    }.joinToString(",\n"))
    println("\t)")
    println("\tval unitsMap = units.associateBy { it.id }\n")
    println("\tval researches = listOf(")
    // Add worker to researches as its more a research than a unit
    println(data.unitDefs.unitDefs.filter { it.id == "worker_unit_id" }.map {unitDef->
        "\t\tResearchDef(0, ${value2String(Int::class.createType(), unitDef.goldCost)}, 0, ${value2String(String::class.createType(), unitDef.iconPath.replace("Splashes/", "Icons/"))}, ${value2String(String::class.createType(), unitDef.id)}, ${value2String(Int::class.createType(), unitDef.mythiumCost)}, 0, 0, ${value2String(String::class.createType(), unitDef.name)}, ${value2String(String::class.createType(), unitDef.tooltip)})"
    }.joinToString(",\n") + ",")
    // Only add only implemented research yet (upgrade_supply_research_id)
    println(data.researches.researches.filter { it.id == "upgrade_supply_research_id"}.map {researchDef->
        "\t\tResearchDef(${ResearchDef::class.memberProperties.map { value2String(it.returnType, it.get(researchDef)) }.joinToString(", ")})"
    }.joinToString(",\n"))
    println("\t)")
    println("\tval researchMap = researches.associateBy { it.id }\n")
    println("\tval waves = listOf(")
    println(data.waveDefs.waveDefs.sortedBy { it.levelNum }.map { waveDef->
        "\t\tWaveDef(${WaveDef::class.memberProperties.map { value2String(it.returnType, it.get(waveDef)) }.joinToString(", ")})"
    }.joinToString(",\n"))
    println("\t)")
    println("\tval global = Global(${Global::class.memberProperties.map { value2String(it.returnType, it.get(data.global)) }.joinToString(", ")})")
    println("}")
}