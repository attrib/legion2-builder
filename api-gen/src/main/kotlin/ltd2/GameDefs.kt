package ltd2

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.stanfy.gsonxml.GsonXmlBuilder
import com.stanfy.gsonxml.XmlParserCreator
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.util.zip.ZipFile

enum class ArmorType(val internalName:String) {
    Immaterial("arm_unarmored"),
    Swift("arm_light"),
    Natural("arm_medium"),
    Arcane("arm_heavy"),
    Fortified("arm_fortified"),
    Illegal("")
}
enum class AttackType(val internalName:String) {
    Pierce("atk_pierce"),
    Impact("atk_normal"),
    Magic("atk_magic"),
    Siege("atk_siege"),
    Pure("atk_chaos"),
    Illegal("")
}
enum class AttackMode(val internalName: String) {
    None("atkmode_none"),
    Melee("atkmode_melee"),
    Ranged("atkmode_ranged"),
    Illegal("")
}

enum class UnitClass(val internalName: String) {
    Fighter("ai_fighter"),
    Creature("ai_creature"),
    Mercenary("ai_attacker"),
    None("ai_none"),
    King("ai_king"),
    Worker("ai_worker"),
    Illegal("")
}
data class Legion(
        @SerializedName("@id") val id:String,
        @SerializedName("playable") val playable:Boolean,
        @SerializedName("iconpath") val iconPath:String,
        @SerializedName("name") val name:String
)
data class Legions(@SerializedName("legion") val legions:List<Legion>)
data class UnitDef(
        @SerializedName("@id") val id:String,
        @SerializedName("legion") val legion:String,
        @SerializedName("unitclass") val unitClass:UnitClass,
        @SerializedName("aspd") val attackSpeed:Double,
        @SerializedName("armortype") val armorType: ArmorType,
        @SerializedName("attackmode") val attackMode:AttackMode,
        @SerializedName("attacktype") val attackType: AttackType,
        @SerializedName("dmgbase") val dmgBase:Int,
        @SerializedName("dmgspread") val dmgSpread:Int,
        @SerializedName("defensebase") val defenseBase:Int,
        @SerializedName("goldbounty") val goldBounty:Int,
        @SerializedName("goldcost") val goldCost:Int,
        @SerializedName("hp") val hitpoints:Int,
        @SerializedName("hpregen") val hitpointsRegen:Double,
        @SerializedName("mp") val mana:Int,
        @SerializedName("mpregen") val manaRegen:Double,
        @SerializedName("mythiumcost") val mythiumCost:Int,
        @SerializedName("splashpath") val iconPath:String,
        @SerializedName("tooltip") val tooltip:String?,
        @SerializedName("name") val name:String,
        @SerializedName("upgradesfrom") val upgradesFrom:String?,
        @SerializedName("totalvalue") val totalValue:Int,
        @SerializedName("totalfood") val totalFood:Int,
        @SerializedName("incomebonus") val incomeBonus:Int
)

data class UnitDefs(@SerializedName("unit") val unitDefs: List<UnitDef>)

data class ResearchDef(
        @SerializedName("@id") val id: String,
        @SerializedName("bonussupply") val bonussupply: Int,
        @SerializedName("mythiumharvestbonus") val mythiumharvestbonus: Int,
        @SerializedName("iconpath") val iconPath: String,
        @SerializedName("tooltip") val tooltip: String,
        @SerializedName("name") val name: String,
        @SerializedName("goldcost") val goldCost:Int,
        @SerializedName("goldcostperlevel") val goldCostPerLevel:Int,
        @SerializedName("mythiumcost") val mythiumCost:Int,
        @SerializedName("mythiumcostperlevel") val mythiumCostPerLevel:Int
)
data class ResearchDefs(@SerializedName("research") val researches: List<ResearchDef>)

data class BuffDef(
        @SerializedName("@id") val id:String,
        @SerializedName("targeteffect") val targetEffect:String?,
        @SerializedName("targeteffectlooping") val targetEffectLooping:Boolean,
        @SerializedName("attacktype") val attackType: AttackType,
        @SerializedName("spelldmgbase") val spellDmgBase: Double,
        @SerializedName("buffdamage") val damage: Double,
        @SerializedName("buffdamagepercentcurrent") val damagePercentCurrent: Double,
        @SerializedName("buffdamagepercentmax") val damagePercentMax: Double,
        @SerializedName("buffhealpersecond") val healPerSecond: Double,
        @SerializedName("legion") val legion: String,
        @SerializedName("maxstacks") val maxStacks: Int,
        @SerializedName("buffatk") val buffAtk: Double,
        @SerializedName("buffatkflat") val buffAtkFlat: Int,
        @SerializedName("buffatk_arcane") val buffAtkArcane: Double,
        @SerializedName("buffatk_fortified") val buffAtkFortified: Double,
        @SerializedName("buffatk_natural") val buffAtkNatural: Double,
        @SerializedName("buffatk_swift") val buffAtkSwift: Double,
        @SerializedName("buffaspd") val aspd: Double,
        @SerializedName("buffdef") val def: Double,
        @SerializedName("buffdefflat") val defFlat: Int,
        @SerializedName("buffimpactreduction") val defImpact: Double,
        @SerializedName("buffmagicreduction") val defMagic: Double,
        @SerializedName("buffpiercereduction") val defPierce: Double,
        @SerializedName("buffhpregen") val hpRegen: Double,
        @SerializedName("buffhpregennatural") val hpRegenNatural: Double,
        @SerializedName("buffhpregenmissing") val hpRegenMissing: Double,
        @SerializedName("buffhpregenflat") val hpRegenFlat: Double,
        @SerializedName("bufflifesteal") val lifeSteal: Double,
        @SerializedName("bufflifestealflat") val lifeStealFlat: Int,
        @SerializedName("buffmpregen") val mpRegen: Double,
        @SerializedName("buffmpregennatural") val mpRegenNatural: Double,
        @SerializedName("buffmpregenflat") val mpRegenFlat: Double,
        @SerializedName("buffmspd") val mspd: Double,
        @SerializedName("buffmspdflat") val mspdFlat: Int,
        @SerializedName("statuses") val statuses: String?,
        @SerializedName("iconpath") val iconPath: String,
        @SerializedName("tooltip") val tooltip: String,
        @SerializedName("name") val name: String
)
data class Buffs(@SerializedName("buff")val buffs:List<BuffDef>)

data class Global(
        @SerializedName("@id") val id:String,
        @SerializedName("attackchartchaos") val attackChaos : DecimalArray,
        @SerializedName("attackchartmagic") val attackMagic : DecimalArray,
        @SerializedName("attackchartnormal") val attackNormal : DecimalArray,
        @SerializedName("attackchartpierce") val attackPierce : DecimalArray,
        @SerializedName("attackchartsiege") val attackSiege : DecimalArray,
        @SerializedName("startinggold") val startingGold: Int,
        @SerializedName("startingmythium") val startingMythium: Int
) {
    fun getModifier(attackType: AttackType, armorType: ArmorType) : Double {
        when(attackType) {
            AttackType.Pierce -> return attackPierce.list[armorType.ordinal]
            AttackType.Impact -> return attackNormal.list[armorType.ordinal]
            AttackType.Magic -> return attackMagic.list[armorType.ordinal]
            AttackType.Siege -> return attackSiege.list[armorType.ordinal]
            AttackType.Pure -> return attackChaos.list[armorType.ordinal]
            AttackType.Illegal -> return 0.0
        }
    }
}
data class Globals(@SerializedName("global") val globals:List<Global>)

data class WaveDef(
        @SerializedName("@id") val id:String,
        @SerializedName("amount") val amount:Int,
        @SerializedName("amount2") val amount2:Int,
        @SerializedName("preparetime") val prepareTime:Int,
        @SerializedName("recommendedvalue") val recommendedValue:Int,
        @SerializedName("unit") val unit: String,
        @SerializedName("spellunit2") val unit2: String?,
        @SerializedName("levelnum") val levelNum:Int,
        @SerializedName("totalreward") val totalReward:Int
)
data class WaveDefs(@SerializedName("wave") val waveDefs:List<WaveDef>) {
}

abstract class PrimitivTypeAdapter<T>(val type:String) : TypeAdapter<T?>() {
    abstract fun convert(value:String) : T?
    override fun read(input: JsonReader): T? {
        val text = input.nextString()
        val split = text.split(":::")
        if( split.size!=2) {
            return convert(text)
        }
        val type = split[0].trim()
        val value = split[1].trim()
        if( (this.type=="*" || type==this.type) && value!="" ) {
            return convert(value)
        } else {
            return null
        }
    }

    override fun write(out: JsonWriter?, value: T?) {
    }
}
class ArmorTypeAdapter : PrimitivTypeAdapter<ArmorType>("preset") {
    override fun convert(value: String): ArmorType? {
        return ArmorType.values().find { it.internalName==value }
    }
}
class AttackTypeAdapter : PrimitivTypeAdapter<AttackType>("preset") {
    override fun convert(value: String): AttackType? {
        return AttackType.values().find { it.internalName==value }
    }
}
class AttackModeAdapter : PrimitivTypeAdapter<AttackMode>("preset") {
    override fun convert(value: String): AttackMode? {
        return AttackMode.values().find { it.internalName==value }
    }
}
class UnitClassAdapter : PrimitivTypeAdapter<UnitClass>("preset") {
    override fun convert(value: String): UnitClass? {
        return UnitClass.values().find { it.internalName==value }
    }
}

class DoubleTypeAdapter : PrimitivTypeAdapter<Double>("double") {
    override fun convert(value: String): Double? {
        return value.toDouble()
    }
}
class IntTypeAdapter : PrimitivTypeAdapter<Int>("int") {
    override fun convert(value: String): Int? {
        return value.toInt()
    }
}
class BooleanTypeAdapter : PrimitivTypeAdapter<Boolean>("*") {
    override fun convert(value: String): Boolean? {
        return value=="True" || value.endsWith("yes")
    }
}
data class DecimalArray(val list:List<Double>) {

}
class DoubleListTypeAdapter : PrimitivTypeAdapter<DecimalArray>("decimalarray") {
    override fun convert(value: String): DecimalArray? {
        return DecimalArray(value.split(",").map { it.toDouble() })
    }
}
class StringTypeAdapter : PrimitivTypeAdapter<String>("*") {
    override fun convert(value: String): String? {
        return value
    }
}

data class GameData(val buffs:Buffs, val legions:Legions, val unitDefs: UnitDefs, val global: Global, val waveDefs: WaveDefs, val researches: ResearchDefs)
fun loadData(ltd2Folder:String) : GameData {
    val p = XmlParserCreator { XmlPullParserFactory.newInstance().newPullParser() }
    val builder = GsonBuilder()
            .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .registerTypeAdapter(String::class.java, StringTypeAdapter())
            .registerTypeAdapter(Int::class.java, IntTypeAdapter())
            .registerTypeAdapter(DecimalArray::class.java, DoubleListTypeAdapter())
            .registerTypeAdapter(AttackType::class.java, AttackTypeAdapter())
            .registerTypeAdapter(ArmorType::class.java, ArmorTypeAdapter())
            .registerTypeAdapter(AttackMode::class.java, AttackModeAdapter())
            .registerTypeAdapter(UnitClass::class.java, UnitClassAdapter())
//            .registerTypeAdapter(Legion::class.java, LegionAdapter())
    val gsonXml = GsonXmlBuilder().wrap(builder).setXmlParserCreator(p).setSameNameLists(true).create()
    val zipFile = "$ltd2Folder/Legion TD 2_Data/StreamingAssets/Maps/legiontd2.zip"
    val zip = ZipFile(zipFile)
    val buffs = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("buffs.xml"))), Buffs::class.java)
    val legions = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("legions.xml"))), Legions::class.java)
    val units = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("units.xml"))), UnitDefs::class.java)
    val globals = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("globals.xml"))), Globals::class.java).globals[0]
    val waves = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("waves.xml"))), WaveDefs::class.java)
    val researches = gsonXml.fromXml(InputStreamReader(zip.getInputStream(zip.getEntry("researches.xml"))), ResearchDefs::class.java)
    return GameData(buffs, legions, units, globals, waves, researches)
}
