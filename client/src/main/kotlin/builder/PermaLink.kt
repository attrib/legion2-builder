package builder

import index.LZString
import kotlinx.serialization.stringFromUtf8Bytes
import kotlinx.serialization.toUtf8Bytes
import ltd2.Build
import ltd2.PermaLinkV1

object PermaLinkV1JS {
    fun toPermaLinkCode(build: Build): String {
        val str = PermaLinkV1.toPermaLinkCode(build).map { it.toChar() }.joinToString("")
        return LZString.compressToBase64("1_$str")
    }
    fun fromPermaLinkCode(code: String) : Build {
        val str = LZString.decompressFromBase64(code)
        val codeId = str[0]
        if( codeId!='1') {
            throw IllegalArgumentException("Illegal code id $codeId")
        }
        return PermaLinkV1.fromPermaLinkCode(str.substring(2).map { it.toByte() }.toByteArray())
    }
}