package ltd2

import kotlinx.serialization.cbor.CBOR

object PermaLinkV1 {
    fun toPermaLinkCode(build: Build): ByteArray {
        return CBOR.dump(BuildSerializer, build)
    }
    fun fromPermaLinkCode(code: ByteArray) : Build {
        return CBOR.load(BuildSerializer, code)
    }

}