package builder

import kotlinext.js.invoke
import org.khronos.webgl.*


external class TextDecoder(encoding: String) {
    fun decode(arr:Uint8Array) : String
}
external class TextEncoder(encoding: String) {
    fun encode(s:String) : Uint8Array
}

@JsModule("datastream-js")
external object DSFactory {
    @JsName("default")
    class DataStream constructor(buffer: ArrayBuffer= definedExternally, byteOffset:Int= definedExternally, endianness:Boolean= definedExternally) {
        var dynamicSize:Boolean
        val byteLength:Int
        var buffer: ArrayBuffer
        var byteOffset:Int

        fun seek(pos:Int)
        fun isEof() : Boolean

        fun writeInt32(v:Int): DataStream
        fun writeInt16(v:Int): DataStream
        fun writeInt8(v:Int): DataStream
        fun writeFloat64(v:Double): DataStream
        fun writeFloat32(v:Float): DataStream

        fun readInt32():Int
        fun readInt16():Int
        fun readInt8():Int
        fun readFloat64():Double
        fun readFloat32():Float

        fun readString(length:Int, encoding:String?= definedExternally):String
        fun writeString(s:String, encoding: String?= definedExternally, length:Int?= definedExternally): DataStream

        fun readUtf8WithLen():String
        fun writeUtf8WithLen(s:String) : DataStream

        companion object {
            val LITTLE_ENDIAN:Boolean
            val BIG_ENDIAN:Boolean
            var endianess : Boolean
        }
    }

}

fun intOrNull(i:Int):Int? = if( i==-1) null else i

fun Uint8Array.asString() : String{
    val sb = StringBuilder()
    (0 until length).forEach {
        val byte = this.get(it).toChar()
        sb.append(byte)
    }
    return sb.toString()
}
fun fromString(s:String) : Uint8Array {
    val a = Uint8Array(s.length)
    s.forEachIndexed { i, v->
        a[i]=v.toByte()
    }
    return a
}


fun DSFactory.DataStream.writeInt8List(v:List<Int>) {
    writeInt8(v.size)
    v.forEach {
        writeInt8(it)
    }
}
fun DSFactory.DataStream.readInt8List() : List<Int> {
    val len = readInt8()
    return (0 until len).map {
        readInt8()
    }
}