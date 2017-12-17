"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _textEncoding = require("text-encoding");

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/**
 * DataStream reads scalars, arrays and structs of data from an ArrayBuffer.
 * It's like a file-like DataView on steroids.
 *
 * @param {ArrayBuffer} arrayBuffer ArrayBuffer to read from.
 * @param {?Number} byteOffset Offset from arrayBuffer beginning for the DataStream.
 * @param {?Boolean} endianness DataStream.BIG_ENDIAN or DataStream.LITTLE_ENDIAN (the default).
 */
var DataStream = function () {
    function DataStream(arrayBuffer, byteOffset) {
        var endianness = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : DataStream.LITTLE_ENDIAN;

        _classCallCheck(this, DataStream);

        this.endianness = endianness;
        this.position = 0;
        /**
         * Whether to extend DataStream buffer when trying to write beyond its size.
         * If set, the buffer is reallocated to twice its current size until the
         * requested write fits the buffer.
         * @type {boolean}
         */
        this._dynamicSize = true;
        /**
         * Virtual byte length of the DataStream backing buffer.
         * Updated to be max of original buffer size and last written size.
         * If dynamicSize is false is set to buffer size.
         * @type {number}
         */
        this._byteLength = 0;
        /**
         * Seek position where DataStream#readStruct ran into a problem.
         * Useful for debugging struct parsing.
         *
         * @type {number}
         */
        this.failurePosition = 0;
        this._byteOffset = byteOffset || 0;
        if (arrayBuffer instanceof ArrayBuffer) {
            this.buffer = arrayBuffer;
        } else if ((typeof arrayBuffer === "undefined" ? "undefined" : _typeof(arrayBuffer)) === "object") {
            this.dataView = arrayBuffer;
            if (byteOffset) {
                this._byteOffset += byteOffset;
            }
        } else {
            this.buffer = new ArrayBuffer(arrayBuffer || 1);
        }
    }

    _createClass(DataStream, [{
        key: "bigEndian",
        value: function bigEndian() {
            this.endianness = DataStream.BIG_ENDIAN;
            return this;
        }
        /**
         * Internal function to resize the DataStream buffer when required.
         * @param {number} extra Number of bytes to add to the buffer allocation.
         * @return {null}
         */

    }, {
        key: "_realloc",
        value: function _realloc(extra) {
            if (!this._dynamicSize) {
                return;
            }
            var req = this._byteOffset + this.position + extra;
            var blen = this._buffer.byteLength;
            if (req <= blen) {
                if (req > this._byteLength) {
                    this._byteLength = req;
                }
                return;
            }
            if (blen < 1) {
                blen = 1;
            }
            while (req > blen) {
                blen *= 2;
            }
            var buf = new ArrayBuffer(blen);
            var src = new Uint8Array(this._buffer);
            var dst = new Uint8Array(buf, 0, src.length);
            dst.set(src);
            this.buffer = buf;
            this._byteLength = req;
        }
        /**
         * Internal function to trim the DataStream buffer when required.
         * Used for stripping out the extra bytes from the backing buffer when
         * the virtual byteLength is smaller than the buffer byteLength (happens after
         * growing the buffer with writes and not filling the extra space completely).
         * @return {null}
         */

    }, {
        key: "_trimAlloc",
        value: function _trimAlloc() {
            if (this._byteLength === this._buffer.byteLength) {
                return;
            }
            var buf = new ArrayBuffer(this._byteLength);
            var dst = new Uint8Array(buf);
            var src = new Uint8Array(this._buffer, 0, dst.length);
            dst.set(src);
            this.buffer = buf;
        }
        /**
         * Sets the DataStream read/write position to given position.
         * Clamps between 0 and DataStream length.
         * @param {number} pos Position to seek to.
         * @return {null}
         */

    }, {
        key: "seek",
        value: function seek(pos) {
            var npos = Math.max(0, Math.min(this.byteLength, pos));
            this.position = isNaN(npos) || !isFinite(npos) ? 0 : npos;
        }
        /**
         * Returns true if the DataStream seek pointer is at the end of buffer and
         * there's no more data to read.
         * @return {boolean} True if the seek pointer is at the end of the buffer.
         */

    }, {
        key: "isEof",
        value: function isEof() {
            return this.position >= this.byteLength;
        }
        /**
         * Maps an Int32Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.
         *
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Int32Array to the DataStream backing buffer.
         */

    }, {
        key: "mapInt32Array",
        value: function mapInt32Array(length, e) {
            this._realloc(length * 4);
            var arr = new Int32Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 4;
            return arr;
        }
        /**
         * Maps an Int16Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.
         *
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Int16Array to the DataStream backing buffer.
         */

    }, {
        key: "mapInt16Array",
        value: function mapInt16Array(length, e) {
            this._realloc(length * 2);
            var arr = new Int16Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 2;
            return arr;
        }
        /**
         * Maps an Int8Array into the DataStream buffer.
         *
         * Nice for quickly reading in data.
         *
         * @param {number} length Number of elements to map.
         * @return {Object} Int8Array to the DataStream backing buffer.
         */

    }, {
        key: "mapInt8Array",
        value: function mapInt8Array(length) {
            this._realloc(length);
            var arr = new Int8Array(this._buffer, this.byteOffset + this.position, length);
            this.position += length;
            return arr;
        }
        /**
         * Maps a Uint32Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.*
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.*
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Uint32Array to the DataStream backing buffer.
         */

    }, {
        key: "mapUint32Array",
        value: function mapUint32Array(length, e) {
            this._realloc(length * 4);
            var arr = new Uint32Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 4;
            return arr;
        }
        /**
         * Maps a Uint16Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.
         *
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Uint16Array to the DataStream backing buffer.
         */

    }, {
        key: "mapUint16Array",
        value: function mapUint16Array(length, e) {
            this._realloc(length * 2);
            var arr = new Uint16Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 2;
            return arr;
        }
        /**
         * Maps a Uint8Array into the DataStream buffer.
         *
         * Nice for quickly reading in data.
         *
         * @param {number} length Number of elements to map.
         * @return {Object} Uint8Array to the DataStream backing buffer.
         */

    }, {
        key: "mapUint8Array",
        value: function mapUint8Array(length) {
            this._realloc(length);
            var arr = new Uint8Array(this._buffer, this.byteOffset + this.position, length);
            this.position += length;
            return arr;
        }
        /**
         * Maps a Float64Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.
         *
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Float64Array to the DataStream backing buffer.
         */

    }, {
        key: "mapFloat64Array",
        value: function mapFloat64Array(length, e) {
            this._realloc(length * 8);
            var arr = new Float64Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 8;
            return arr;
        }
        /**
         * Maps a Float32Array into the DataStream buffer, swizzling it to native
         * endianness in-place. The current offset from the start of the buffer needs to
         * be a multiple of element size, just like with typed array views.
         *
         * Nice for quickly reading in data. Warning: potentially modifies the buffer
         * contents.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} Float32Array to the DataStream backing buffer.
         */

    }, {
        key: "mapFloat32Array",
        value: function mapFloat32Array(length, e) {
            this._realloc(length * 4);
            var arr = new Float32Array(this._buffer, this.byteOffset + this.position, length);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += length * 4;
            return arr;
        }
        /**
         * Reads an Int32Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Int32Array.
         */

    }, {
        key: "readInt32Array",
        value: function readInt32Array(length, e) {
            length = length == null ? this.byteLength - this.position / 4 : length;
            var arr = new Int32Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads an Int16Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Int16Array.
         */

    }, {
        key: "readInt16Array",
        value: function readInt16Array(length, e) {
            length = length == null ? this.byteLength - this.position / 2 : length;
            var arr = new Int16Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads an Int8Array of desired length from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @return {Object} The read Int8Array.
         */

    }, {
        key: "readInt8Array",
        value: function readInt8Array(length) {
            length = length == null ? this.byteLength - this.position : length;
            var arr = new Int8Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads a Uint32Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Uint32Array.
         */

    }, {
        key: "readUint32Array",
        value: function readUint32Array(length, e) {
            length = length == null ? this.byteLength - this.position / 4 : length;
            var arr = new Uint32Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads a Uint16Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Uint16Array.
         */

    }, {
        key: "readUint16Array",
        value: function readUint16Array(length, e) {
            length = length == null ? this.byteLength - this.position / 2 : length;
            var arr = new Uint16Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads a Uint8Array of desired length from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @return {Object} The read Uint8Array.
         */

    }, {
        key: "readUint8Array",
        value: function readUint8Array(length) {
            length = length == null ? this.byteLength - this.position : length;
            var arr = new Uint8Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads a Float64Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Float64Array.
         */

    }, {
        key: "readFloat64Array",
        value: function readFloat64Array(length, e) {
            length = length == null ? this.byteLength - this.position / 8 : length;
            var arr = new Float64Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Reads a Float32Array of desired length and endianness from the DataStream.
         *
         * @param {number} length Number of elements to map.
         * @param {?boolean} e Endianness of the data to read.
         * @return {Object} The read Float32Array.
         */

    }, {
        key: "readFloat32Array",
        value: function readFloat32Array(length, e) {
            length = length == null ? this.byteLength - this.position / 4 : length;
            var arr = new Float32Array(length);
            DataStream.memcpy(arr.buffer, 0, this.buffer, this.byteOffset + this.position, length * arr.BYTES_PER_ELEMENT);
            DataStream.arrayToNative(arr, e == null ? this.endianness : e);
            this.position += arr.byteLength;
            return arr;
        }
        /**
         * Writes an Int32Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeInt32Array",
        value: function writeInt32Array(arr, e) {
            this._realloc(arr.length * 4);
            if (arr instanceof Int32Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapInt32Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeInt32(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Writes an Int16Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeInt16Array",
        value: function writeInt16Array(arr, e) {
            this._realloc(arr.length * 2);
            if (arr instanceof Int16Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapInt16Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeInt16(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Writes an Int8Array to the DataStream.
         *
         * @param {Object} arr The array to write.
         */

    }, {
        key: "writeInt8Array",
        value: function writeInt8Array(arr) {
            this._realloc(arr.length);
            if (arr instanceof Int8Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapInt8Array(arr.length);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeInt8(arr[i]);
                }
            }
            return this;
        }
        /**
         * Writes a Uint32Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeUint32Array",
        value: function writeUint32Array(arr, e) {
            this._realloc(arr.length * 4);
            if (arr instanceof Uint32Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapUint32Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeUint32(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Writes a Uint16Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeUint16Array",
        value: function writeUint16Array(arr, e) {
            this._realloc(arr.length * 2);
            if (arr instanceof Uint16Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapUint16Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeUint16(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Writes a Uint8Array to the DataStream.
         *
         * @param {Object} arr The array to write.
         */

    }, {
        key: "writeUint8Array",
        value: function writeUint8Array(arr) {
            this._realloc(arr.length);
            if (arr instanceof Uint8Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapUint8Array(arr.length);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeUint8(arr[i]);
                }
            }
            return this;
        }
        /**
         * Writes a Float64Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeFloat64Array",
        value: function writeFloat64Array(arr, e) {
            this._realloc(arr.length * 8);
            if (arr instanceof Float64Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapFloat64Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeFloat64(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Writes a Float32Array of specified endianness to the DataStream.
         *
         * @param {Object} arr The array to write.
         * @param {?boolean} e Endianness of the data to write.
         */

    }, {
        key: "writeFloat32Array",
        value: function writeFloat32Array(arr, e) {
            this._realloc(arr.length * 4);
            if (arr instanceof Float32Array && (this.byteOffset + this.position) % arr.BYTES_PER_ELEMENT === 0) {
                DataStream.memcpy(this._buffer, this.byteOffset + this.position, arr.buffer, arr.byteOffset, arr.byteLength);
                this.mapFloat32Array(arr.length, e);
            } else {
                // tslint:disable-next-line prefer-for-of
                for (var i = 0; i < arr.length; i++) {
                    this.writeFloat32(arr[i], e);
                }
            }
            return this;
        }
        /**
         * Reads a 32-bit int from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readInt32",
        value: function readInt32(e) {
            var v = this._dataView.getInt32(this.position, e == null ? this.endianness : e);
            this.position += 4;
            return v;
        }
        /**
         * Reads a 16-bit int from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readInt16",
        value: function readInt16(e) {
            var v = this._dataView.getInt16(this.position, e == null ? this.endianness : e);
            this.position += 2;
            return v;
        }
        /**
         * Reads an 8-bit int from the DataStream.
         *
         * @return {number} The read number.
         */

    }, {
        key: "readInt8",
        value: function readInt8() {
            var v = this._dataView.getInt8(this.position);
            this.position += 1;
            return v;
        }
        /**
         * Reads a 32-bit unsigned int from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readUint32",
        value: function readUint32(e) {
            var v = this._dataView.getUint32(this.position, e == null ? this.endianness : e);
            this.position += 4;
            return v;
        }
        /**
         * Reads a 16-bit unsigned int from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readUint16",
        value: function readUint16(e) {
            var v = this._dataView.getUint16(this.position, e == null ? this.endianness : e);
            this.position += 2;
            return v;
        }
        /**
         * Reads an 8-bit unsigned int from the DataStream.
         *
         * @return {number} The read number.
         */

    }, {
        key: "readUint8",
        value: function readUint8() {
            var v = this._dataView.getUint8(this.position);
            this.position += 1;
            return v;
        }
        /**
         * Reads a 32-bit float from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readFloat32",
        value: function readFloat32(e) {
            var v = this._dataView.getFloat32(this.position, e == null ? this.endianness : e);
            this.position += 4;
            return v;
        }
        /**
         * Reads a 64-bit float from the DataStream with the desired endianness.
         *
         * @param {?boolean} e Endianness of the number.
         * @return {number} The read number.
         */

    }, {
        key: "readFloat64",
        value: function readFloat64(e) {
            var v = this._dataView.getFloat64(this.position, e == null ? this.endianness : e);
            this.position += 8;
            return v;
        }
        /**
         * Writes a 32-bit int to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeInt32",
        value: function writeInt32(v, e) {
            this._realloc(4);
            this._dataView.setInt32(this.position, v, e == null ? this.endianness : e);
            this.position += 4;
            return this;
        }
        /**
         * Writes a 16-bit int to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeInt16",
        value: function writeInt16(v, e) {
            this._realloc(2);
            this._dataView.setInt16(this.position, v, e == null ? this.endianness : e);
            this.position += 2;
            return this;
        }
        /**
         * Writes an 8-bit int to the DataStream.
         *
         * @param {number} v Number to write.
         */

    }, {
        key: "writeInt8",
        value: function writeInt8(v) {
            this._realloc(1);
            this._dataView.setInt8(this.position, v);
            this.position += 1;
            return this;
        }
        /**
         * Writes a 32-bit unsigned int to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeUint32",
        value: function writeUint32(v, e) {
            this._realloc(4);
            this._dataView.setUint32(this.position, v, e == null ? this.endianness : e);
            this.position += 4;
            return this;
        }
        /**
         * Writes a 16-bit unsigned int to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeUint16",
        value: function writeUint16(v, e) {
            this._realloc(2);
            this._dataView.setUint16(this.position, v, e == null ? this.endianness : e);
            this.position += 2;
            return this;
        }
        /**
         * Writes an 8-bit unsigned  int to the DataStream.
         *
         * @param {number} v Number to write.
         */

    }, {
        key: "writeUint8",
        value: function writeUint8(v) {
            this._realloc(1);
            this._dataView.setUint8(this.position, v);
            this.position += 1;
            return this;
        }
        /**
         * Writes a 32-bit float to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeFloat32",
        value: function writeFloat32(v, e) {
            this._realloc(4);
            this._dataView.setFloat32(this.position, v, e == null ? this.endianness : e);
            this.position += 4;
            return this;
        }
        /**
         * Writes a 64-bit float to the DataStream with the desired endianness.
         *
         * @param {number} v Number to write.
         * @param {?boolean} e Endianness of the number.
         */

    }, {
        key: "writeFloat64",
        value: function writeFloat64(v, e) {
            this._realloc(8);
            this._dataView.setFloat64(this.position, v, e == null ? this.endianness : e);
            this.position += 8;
            return this;
        }
        /**
         * Copies byteLength bytes from the src buffer at srcOffset to the
         * dst buffer at dstOffset.
         *
         * @param {Object} dst Destination ArrayBuffer to write to.
         * @param {number} dstOffset Offset to the destination ArrayBuffer.
         * @param {Object} src Source ArrayBuffer to read from.
         * @param {number} srcOffset Offset to the source ArrayBuffer.
         * @param {number} byteLength Number of bytes to copy.
         */

    }, {
        key: "readStruct",

        /**
         * Reads a struct of data from the DataStream. The struct is defined as
         * a flat array of [name, type]-pairs. See the example below:
         *
         * ds.readStruct([
         * 'headerTag', 'uint32', // Uint32 in DataStream endianness.
         * 'headerTag2', 'uint32be', // Big-endian Uint32.
         * 'headerTag3', 'uint32le', // Little-endian Uint32.
         * 'array', ['[]', 'uint32', 16], // Uint32Array of length 16.
         * 'array2Length', 'uint32',
         * 'array2', ['[]', 'uint32', 'array2Length'] // Uint32Array of length array2Length
         * ]);
         *
         * The possible values for the type are as follows:
         *
         * // Number types
         *
         * // Unsuffixed number types use DataStream endianness.
         * // To explicitly specify endianness, suffix the type with
         * // 'le' for little-endian or 'be' for big-endian,
         * // e.g. 'int32be' for big-endian int32.
         *
         * 'uint8' -- 8-bit unsigned int
         * 'uint16' -- 16-bit unsigned int
         * 'uint32' -- 32-bit unsigned int
         * 'int8' -- 8-bit int
         * 'int16' -- 16-bit int
         * 'int32' -- 32-bit int
         * 'float32' -- 32-bit float
         * 'float64' -- 64-bit float
         *
         * // String types
         * 'cstring' -- ASCII string terminated by a zero byte.
         * 'string:N' -- ASCII string of length N, where N is a literal integer.
         * 'string:variableName' -- ASCII string of length $variableName,
         * where 'variableName' is a previously parsed number in the current struct.
         * 'string,CHARSET:N' -- String of byteLength N encoded with given CHARSET.
         * 'u16string:N' -- UCS-2 string of length N in DataStream endianness.
         * 'u16stringle:N' -- UCS-2 string of length N in little-endian.
         * 'u16stringbe:N' -- UCS-2 string of length N in big-endian.
         *
         * // Complex types
         * [name, type, name_2, type_2, ..., name_N, type_N] -- Struct
         * function(dataStream, struct) {} -- Callback function to read and return data.
         * {get: function(dataStream, struct) {},
         *  set: function(dataStream, struct) {}}
         * -- Getter/setter functions to read and return data, handy for using the same
         * struct definition for reading and writing structs.
         * ['[]', type, length] -- Array of given type and length. The length can be either
         * a number, a string that references a previously-read
         * field, or a callback function(struct, dataStream, type){}.
         * If length is '*', reads in as many elements as it can.
         *
         * @param {Object} structDefinition Struct definition object.
         * @return {Object} The read struct. Null if failed to read struct.
         *
         * @deprecated use DataStream.read/write(TypeDef) instead of readStruct/writeStruct
         */
        value: function readStruct(structDefinition) {
            var struct = {};
            var t = void 0;
            var v = void 0;
            var p = this.position;
            for (var i = 0; i < structDefinition.length; i += 2) {
                t = structDefinition[i + 1];
                v = this.readType(t, struct);
                if (v == null) {
                    if (this.failurePosition === 0) {
                        this.failurePosition = this.position;
                    }
                    this.position = p;
                    return null;
                }
                struct[structDefinition[i]] = v;
            }
            return struct;
        }
        /** ex:
         * const def = [
         *      ["obj", [["num", "Int8"],
         *               ["greet", "Utf8WithLen"],
         *               ["a1", "Int16*"]]
         *      ],
         *      ["a2", "Uint16*"]
         *  ];
         *  const o = {obj: {
         *          num: 5,
         *          greet: "Xin chào",
         *          a1: [-3, 0, 4, 9, 0x7FFF],
         *      },
         *      a2: [3, 0, 4, 9, 0xFFFF]
         *  });
         *  ds.write(def, o);
         *  expect: new DataStream(ds.buffer).read(def) deepEqual o
         */

    }, {
        key: "read",
        value: function read(def) {
            var o = {};
            var d = void 0;
            var _iteratorNormalCompletion = true;
            var _didIteratorError = false;
            var _iteratorError = undefined;

            try {
                for (var _iterator = def[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                    d = _step.value;

                    var v = d[0];
                    var t = d[1];
                    if (typeof t === "string") {
                        if (t.endsWith("*")) {
                            var len = this.readUint16();
                            o[v] = this["read" + t.substr(0, t.length - 1) + "Array"](len);
                        } else {
                            o[v] = this["read" + t]();
                        }
                    } else {
                        o[v] = this.read(t);
                    }
                }
            } catch (err) {
                _didIteratorError = true;
                _iteratorError = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion && _iterator.return) {
                        _iterator.return();
                    }
                } finally {
                    if (_didIteratorError) {
                        throw _iteratorError;
                    }
                }
            }

            return o;
        }
        /** ex:
         * const def = [
         *      ["obj", [["num", "Int8"],
         *               ["greet", "Utf8WithLen"],
         *               ["a1", "Int16*"]]
         *      ],
         *      ["a2", "Uint16*"]
         *  ];
         *  const o = {obj: {
         *          num: 5,
         *          greet: "Xin chào",
         *          a1: [-3, 0, 4, 9, 0x7FFF],
         *      },
         *      a2: [3, 0, 4, 9, 0xFFFF]
         *  });
         *  ds.write(def, o);
         *  expect: new DataStream(ds.buffer).read(def) deepEqual o
         */

    }, {
        key: "write",
        value: function write(def, o) {
            var d = void 0;
            var _iteratorNormalCompletion2 = true;
            var _didIteratorError2 = false;
            var _iteratorError2 = undefined;

            try {
                for (var _iterator2 = def[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
                    d = _step2.value;

                    var v = d[0];
                    var t = d[1];
                    if (typeof t === "string") {
                        if (t.endsWith("*")) {
                            var arr = o[v];
                            this.writeUint16(arr.length);
                            this["write" + t.substr(0, t.length - 1) + "Array"](arr);
                        } else {
                            this["write" + t](o[v]);
                        }
                    } else {
                        this.write(t, o[v]);
                    }
                }
            } catch (err) {
                _didIteratorError2 = true;
                _iteratorError2 = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion2 && _iterator2.return) {
                        _iterator2.return();
                    }
                } finally {
                    if (_didIteratorError2) {
                        throw _iteratorError2;
                    }
                }
            }

            return this;
        }
        /** convenient method to write data. ex, instead of write data as in jsdoc of `write` method, we can:
         * const def = [
         *      ["Int8", "Utf8WithLen", "Int16*"],
         *      "Uint16*"
         *  ];
         *  const a = [
         *      [5, "Xin chào", [-3, 0, 4, 9, 0x7FFF]],
         *      [3, 0, 4, 9, 0xFFFF]
         *  ];
         *  ds.writeArray(def, a)
         */

    }, {
        key: "writeArray",
        value: function writeArray(def, a) {
            var t = void 0;
            var i = void 0;
            for (i = 0; i < def.length; i++) {
                t = def[i];
                if (typeof t === "string") {
                    if (t.endsWith("*")) {
                        var arr = a[i];
                        this.writeUint16(arr.length);
                        this["write" + t.substr(0, t.length - 1) + "Array"](arr);
                    } else {
                        this["write" + t](a[i]);
                    }
                } else {
                    this.writeArray(t, a[i]);
                }
            }
            return this;
        }
        /**
         * Read UCS-2 string of desired length and endianness from the DataStream.
         *
         * @param {number} length The length of the string to read.
         * @param {boolean} endianness The endianness of the string data in the DataStream.
         * @return {string} The read string.
         */

    }, {
        key: "readUCS2String",
        value: function readUCS2String(length, endianness) {
            return DataStream.createStringFromArray(this.readUint16Array(length, endianness));
        }
        /**
         * Write a UCS-2 string of desired endianness to the DataStream. The
         * lengthOverride argument lets you define the number of characters to write.
         * If the string is shorter than lengthOverride, the extra space is padded with
         * zeroes.
         *
         * @param {string} str The string to write.
         * @param {?boolean} endianness The endianness to use for the written string data.
         * @param {?number} lengthOverride The number of characters to write.
         */

    }, {
        key: "writeUCS2String",
        value: function writeUCS2String(str, endianness, lengthOverride) {
            if (lengthOverride == null) {
                lengthOverride = str.length;
            }
            var i = 0;
            for (; i < str.length && i < lengthOverride; i++) {
                this.writeUint16(str.charCodeAt(i), endianness);
            }
            for (; i < lengthOverride; i++) {
                this.writeUint16(0);
            }
            return this;
        }
        /**
         * Read a string of desired length and encoding from the DataStream.
         *
         * @param {number} length The length of the string to read in bytes.
         * @param {?string} encoding The encoding of the string data in the DataStream.
         * Defaults to ASCII.
         * @return {string} The read string.
         */

    }, {
        key: "readString",
        value: function readString(length, encoding) {
            if (encoding == null || encoding === "ASCII") {
                return DataStream.createStringFromArray(this.mapUint8Array(length == null ? this.byteLength - this.position : length));
            } else {
                return new _textEncoding.TextDecoder(encoding).decode(this.mapUint8Array(length));
            }
        }
        /**
         * Writes a string of desired length and encoding to the DataStream.
         *
         * @param {string} s The string to write.
         * @param {?string} encoding The encoding for the written string data.
         * Defaults to ASCII.
         * @param {?number} length The number of characters to write.
         */

    }, {
        key: "writeString",
        value: function writeString(s, encoding, length) {
            if (encoding == null || encoding === "ASCII") {
                if (length != null) {
                    var i = void 0;
                    var len = Math.min(s.length, length);
                    for (i = 0; i < len; i++) {
                        this.writeUint8(s.charCodeAt(i));
                    }
                    for (; i < length; i++) {
                        this.writeUint8(0);
                    }
                } else {
                    for (var _i = 0; _i < s.length; _i++) {
                        this.writeUint8(s.charCodeAt(_i));
                    }
                }
            } else {
                this.writeUint8Array(new _textEncoding.TextEncoder(encoding).encode(s.substring(0, length)));
            }
            return this;
        }
        /** writeUint16(utf8 length of `s`) then write utf8 `s` */

    }, {
        key: "writeUtf8WithLen",
        value: function writeUtf8WithLen(s) {
            var arr = new _textEncoding.TextEncoder("utf-8").encode(s);
            return this.writeUint16(arr.length).writeUint8Array(arr);
        }
        /** readUint16 into `len` then read `len` Uint8 then parse into the result utf8 string */

    }, {
        key: "readUtf8WithLen",
        value: function readUtf8WithLen() {
            var len = this.readUint16();
            return new _textEncoding.TextDecoder("utf-8").decode(this.mapUint8Array(len));
        }
        /**
         * Read null-terminated string of desired length from the DataStream. Truncates
         * the returned string so that the null byte is not a part of it.
         *
         * @param {?number} length The length of the string to read.
         * @return {string} The read string.
         */

    }, {
        key: "readCString",
        value: function readCString(length) {
            var blen = this.byteLength - this.position;
            var u8 = new Uint8Array(this._buffer, this._byteOffset + this.position);
            var len = blen;
            if (length != null) {
                len = Math.min(length, blen);
            }
            var i = 0;
            for (; i < len && u8[i] !== 0; i++) {
                // find first zero byte
            }
            var s = DataStream.createStringFromArray(this.mapUint8Array(i));
            if (length != null) {
                this.position += len - i;
            } else if (i !== blen) {
                this.position += 1; // trailing zero if not at end of buffer
            }
            return s;
        }
        /**
         * Writes a null-terminated string to DataStream and zero-pads it to length
         * bytes. If length is not given, writes the string followed by a zero.
         * If string is longer than length, the written part of the string does not have
         * a trailing zero.
         *
         * @param {string} s The string to write.
         * @param {?number} length The number of characters to write.
         */

    }, {
        key: "writeCString",
        value: function writeCString(s, length) {
            if (length != null) {
                var i = void 0;
                var len = Math.min(s.length, length);
                for (i = 0; i < len; i++) {
                    this.writeUint8(s.charCodeAt(i));
                }
                for (; i < length; i++) {
                    this.writeUint8(0);
                }
            } else {
                for (var _i2 = 0; _i2 < s.length; _i2++) {
                    this.writeUint8(s.charCodeAt(_i2));
                }
                this.writeUint8(0);
            }
            return this;
        }
        /**
         * Reads an object of type t from the DataStream, passing struct as the thus-far
         * read struct to possible callbacks that refer to it. Used by readStruct for
         * reading in the values, so the type is one of the readStruct types.
         *
         * @param {Object} t Type of the object to read.
         * @param {?Object} struct Struct to refer to when resolving length references
         * and for calling callbacks.
         * @return {?Object} Returns the object on successful read, null on unsuccessful.
         */

    }, {
        key: "readType",
        value: function readType(t, struct) {
            if (typeof t === "function") {
                return t(this, struct);
            } else if ((typeof t === "undefined" ? "undefined" : _typeof(t)) === "object" && !(t instanceof Array)) {
                return t.get(this, struct);
            } else if (t instanceof Array && t.length !== 3) {
                return this.readStruct(t);
            }
            var v = null;
            var lengthOverride = null;
            var charset = "ASCII";
            var pos = this.position;
            if (typeof t === "string" && /:/.test(t)) {
                var tp = t.split(":");
                t = tp[0];
                var len = tp[1];
                // allow length to be previously parsed variable
                // e.g. 'string:fieldLength', if `fieldLength` has been parsed previously.
                // else, assume literal integer e.g., 'string:4'
                lengthOverride = parseInt(struct[len] != null ? struct[len] : len, 10);
            }
            if (typeof t === "string" && /,/.test(t)) {
                var _tp = t.split(",");
                t = _tp[0];
                charset = _tp[1];
            }
            switch (t) {
                case "uint8":
                    v = this.readUint8();
                    break;
                case "int8":
                    v = this.readInt8();
                    break;
                case "uint16":
                    v = this.readUint16(this.endianness);
                    break;
                case "int16":
                    v = this.readInt16(this.endianness);
                    break;
                case "uint32":
                    v = this.readUint32(this.endianness);
                    break;
                case "int32":
                    v = this.readInt32(this.endianness);
                    break;
                case "float32":
                    v = this.readFloat32(this.endianness);
                    break;
                case "float64":
                    v = this.readFloat64(this.endianness);
                    break;
                case "uint16be":
                    v = this.readUint16(DataStream.BIG_ENDIAN);
                    break;
                case "int16be":
                    v = this.readInt16(DataStream.BIG_ENDIAN);
                    break;
                case "uint32be":
                    v = this.readUint32(DataStream.BIG_ENDIAN);
                    break;
                case "int32be":
                    v = this.readInt32(DataStream.BIG_ENDIAN);
                    break;
                case "float32be":
                    v = this.readFloat32(DataStream.BIG_ENDIAN);
                    break;
                case "float64be":
                    v = this.readFloat64(DataStream.BIG_ENDIAN);
                    break;
                case "uint16le":
                    v = this.readUint16(DataStream.LITTLE_ENDIAN);
                    break;
                case "int16le":
                    v = this.readInt16(DataStream.LITTLE_ENDIAN);
                    break;
                case "uint32le":
                    v = this.readUint32(DataStream.LITTLE_ENDIAN);
                    break;
                case "int32le":
                    v = this.readInt32(DataStream.LITTLE_ENDIAN);
                    break;
                case "float32le":
                    v = this.readFloat32(DataStream.LITTLE_ENDIAN);
                    break;
                case "float64le":
                    v = this.readFloat64(DataStream.LITTLE_ENDIAN);
                    break;
                case "cstring":
                    v = this.readCString(lengthOverride);
                    break;
                case "string":
                    v = this.readString(lengthOverride, charset);
                    break;
                case "u16string":
                    v = this.readUCS2String(lengthOverride, this.endianness);
                    break;
                case "u16stringle":
                    v = this.readUCS2String(lengthOverride, DataStream.LITTLE_ENDIAN);
                    break;
                case "u16stringbe":
                    v = this.readUCS2String(lengthOverride, DataStream.BIG_ENDIAN);
                    break;
                default:
                    if (t.length === 3) {
                        var ta = t[1];
                        var _len = t[2];
                        var length = 0;
                        if (typeof _len === "function") {
                            length = _len(struct, this, t);
                        } else if (typeof _len === "string" && struct[_len] != null) {
                            length = parseInt(struct[_len], 10);
                        } else {
                            length = parseInt(_len, 10);
                        }
                        if (typeof ta === "string") {
                            var tap = ta.replace(/(le|be)$/, "");
                            var endianness = null;
                            if (/le$/.test(ta)) {
                                endianness = DataStream.LITTLE_ENDIAN;
                            } else if (/be$/.test(ta)) {
                                endianness = DataStream.BIG_ENDIAN;
                            }
                            if (_len === "*") {
                                length = null;
                            }
                            switch (tap) {
                                case "uint8":
                                    v = this.readUint8Array(length);
                                    break;
                                case "uint16":
                                    v = this.readUint16Array(length, endianness);
                                    break;
                                case "uint32":
                                    v = this.readUint32Array(length, endianness);
                                    break;
                                case "int8":
                                    v = this.readInt8Array(length);
                                    break;
                                case "int16":
                                    v = this.readInt16Array(length, endianness);
                                    break;
                                case "int32":
                                    v = this.readInt32Array(length, endianness);
                                    break;
                                case "float32":
                                    v = this.readFloat32Array(length, endianness);
                                    break;
                                case "float64":
                                    v = this.readFloat64Array(length, endianness);
                                    break;
                                case "cstring":
                                case "utf16string":
                                case "string":
                                    if (length == null) {
                                        v = [];
                                        while (!this.isEof()) {
                                            var u = this.readType(ta, struct);
                                            if (u == null) break;
                                            v.push(u);
                                        }
                                    } else {
                                        v = new Array(length);
                                        for (var i = 0; i < length; i++) {
                                            v[i] = this.readType(ta, struct);
                                        }
                                    }
                                    break;
                            }
                        } else {
                            if (_len === "*") {
                                v = [];
                                while (true) {
                                    var p = this.position;
                                    try {
                                        var o = this.readType(ta, struct);
                                        if (o == null) {
                                            this.position = p;
                                            break;
                                        }
                                        v.push(o);
                                    } catch (e) {
                                        this.position = p;
                                        break;
                                    }
                                }
                            } else {
                                v = new Array(length);
                                for (var _i3 = 0; _i3 < length; _i3++) {
                                    var _u = this.readType(ta, struct);
                                    if (_u == null) return null;
                                    v[_i3] = _u;
                                }
                            }
                        }
                        break;
                    }
            }
            if (lengthOverride != null) {
                this.position = pos + lengthOverride;
            }
            return v;
        }
        /**
         * Writes a struct to the DataStream. Takes a structDefinition that gives the
         * types and a struct object that gives the values. Refer to readStruct for the
         * structure of structDefinition.
         *
         * @param {Object} structDefinition Type definition of the struct.
         * @param {Object} struct The struct data object.
         * @param needConvertStructDef if set (== true) then structDefinition will be convert using
         *        `DataStream.defWriteStruct` before writing.
         *
         * @deprecated use DataStream.read/write(TypeDef) instead of readStruct/writeStruct
         */

    }, {
        key: "writeStruct",
        value: function writeStruct(structDefinition, struct) {
            var needConvertStructDef = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : false;

            if (needConvertStructDef) {
                structDefinition = DataStream.defWriteStruct(structDefinition, struct);
            }
            for (var i = 0; i < structDefinition.length; i += 2) {
                var t = structDefinition[i + 1];
                this.writeType(t, struct[structDefinition[i]], struct);
            }
            return this;
        }
        /**
         * Convert a struct definition using for `readStruct` to a struct definition that can be using for `writeStruct`
         * @param readStructDef ex ['len', 'uint8', 'greet', 'string,utf-8:some_len_var_name']
         * @param struct The actual struct will be writing, ex {greet: 'Xin Chào'}
         * @return {Array<*>} the readStructDef with all string type that has encoding specified
         *          (ex 'string,utf-8:some_len_var_name')
         *            be replaced by a `function` that write the correspond string field in `struct` (ex, struct.greet)
         * @side-effect struct is modified: struct.<some_len_var_name> is set = length of the string field
         *          (ex, struct.greet) after encode.
         */

    }, {
        key: "writeType",

        /**
         * Writes object v of type t to the DataStream.
         *
         * @param {Object} t Type of data to write.
         * @param {Object} v Value of data to write.
         * @param {Object} struct Struct to pass to write callback functions.
         */
        value: function writeType(t, v, struct) {
            if (typeof t === "function") {
                t(this, v, struct);
                return this;
            } else if ((typeof t === "undefined" ? "undefined" : _typeof(t)) === "object" && !(t instanceof Array)) {
                t.set(this, v, struct);
                return this;
            }
            var lengthOverride = null;
            var charset = "ASCII";
            var pos = this.position;
            if (typeof t === "string" && /:/.test(t)) {
                var tp = t.split(":");
                t = tp[0];
                var len = tp[1];
                // allow length to be previously parsed variable
                // e.g. 'string:fieldLength', if `fieldLength` has been parsed previously.
                // else, assume literal integer e.g., 'string:4'
                lengthOverride = parseInt(struct[len] != null ? struct[len] : len, 10);
            }
            if (typeof t === "string" && /,/.test(t)) {
                var _tp2 = t.split(",");
                t = _tp2[0];
                charset = _tp2[1];
            }
            switch (t) {
                case "uint8":
                    this.writeUint8(v);
                    break;
                case "int8":
                    this.writeInt8(v);
                    break;
                case "uint16":
                    this.writeUint16(v, this.endianness);
                    break;
                case "int16":
                    this.writeInt16(v, this.endianness);
                    break;
                case "uint32":
                    this.writeUint32(v, this.endianness);
                    break;
                case "int32":
                    this.writeInt32(v, this.endianness);
                    break;
                case "float32":
                    this.writeFloat32(v, this.endianness);
                    break;
                case "float64":
                    this.writeFloat64(v, this.endianness);
                    break;
                case "uint16be":
                    this.writeUint16(v, DataStream.BIG_ENDIAN);
                    break;
                case "int16be":
                    this.writeInt16(v, DataStream.BIG_ENDIAN);
                    break;
                case "uint32be":
                    this.writeUint32(v, DataStream.BIG_ENDIAN);
                    break;
                case "int32be":
                    this.writeInt32(v, DataStream.BIG_ENDIAN);
                    break;
                case "float32be":
                    this.writeFloat32(v, DataStream.BIG_ENDIAN);
                    break;
                case "float64be":
                    this.writeFloat64(v, DataStream.BIG_ENDIAN);
                    break;
                case "uint16le":
                    this.writeUint16(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "int16le":
                    this.writeInt16(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "uint32le":
                    this.writeUint32(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "int32le":
                    this.writeInt32(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "float32le":
                    this.writeFloat32(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "float64le":
                    this.writeFloat64(v, DataStream.LITTLE_ENDIAN);
                    break;
                case "cstring":
                    this.writeCString(v, lengthOverride);
                    break;
                case "string":
                    this.writeString(v, charset, lengthOverride);
                    break;
                case "u16string":
                    this.writeUCS2String(v, this.endianness, lengthOverride);
                    break;
                case "u16stringle":
                    this.writeUCS2String(v, DataStream.LITTLE_ENDIAN, lengthOverride);
                    break;
                case "u16stringbe":
                    this.writeUCS2String(v, DataStream.BIG_ENDIAN, lengthOverride);
                    break;
                default:
                    // t instanceof Array
                    if (t.length === 3) {
                        var ta = t[1];
                        var _iteratorNormalCompletion3 = true;
                        var _didIteratorError3 = false;
                        var _iteratorError3 = undefined;

                        try {
                            for (var _iterator3 = v[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
                                var vi = _step3.value;

                                this.writeType(ta, vi, struct);
                            }
                        } catch (err) {
                            _didIteratorError3 = true;
                            _iteratorError3 = err;
                        } finally {
                            try {
                                if (!_iteratorNormalCompletion3 && _iterator3.return) {
                                    _iterator3.return();
                                }
                            } finally {
                                if (_didIteratorError3) {
                                    throw _iteratorError3;
                                }
                            }
                        }

                        break;
                    } else {
                        this.writeStruct(t, v);
                        break;
                    }
            }
            if (lengthOverride != null) {
                this.position = pos;
                this._realloc(lengthOverride);
                this.position = pos + lengthOverride;
            }
            return this;
        }
    }, {
        key: "dynamicSize",
        get: function get() {
            return this._dynamicSize;
        },
        set: function set(v) {
            if (!v) {
                this._trimAlloc();
            }
            this._dynamicSize = v;
        }
        /**
         * Returns the byte length of the DataStream object.
         * @type {number}
         */

    }, {
        key: "byteLength",
        get: function get() {
            return this._byteLength - this._byteOffset;
        }
        /**
         * Set/get the backing ArrayBuffer of the DataStream object.
         * The setter updates the DataView to point to the new buffer.
         * @type {Object}
         */

    }, {
        key: "buffer",
        get: function get() {
            this._trimAlloc();
            return this._buffer;
        },
        set: function set(v) {
            this._buffer = v;
            this._dataView = new DataView(this._buffer, this._byteOffset);
            this._byteLength = this._buffer.byteLength;
        }
        /**
         * Set/get the byteOffset of the DataStream object.
         * The setter updates the DataView to point to the new byteOffset.
         * @type {number}
         */

    }, {
        key: "byteOffset",
        get: function get() {
            return this._byteOffset;
        },
        set: function set(v) {
            this._byteOffset = v;
            this._dataView = new DataView(this._buffer, this._byteOffset);
            this._byteLength = this._buffer.byteLength;
        }
        /**
         * Set/get the backing DataView of the DataStream object.
         * The setter updates the buffer and byteOffset to point to the DataView values.
         * @type get: DataView, set: {buffer: ArrayBuffer, byteOffset: number, byteLength: number}
         */

    }, {
        key: "dataView",
        get: function get() {
            return this._dataView;
        },
        set: function set(v) {
            this._byteOffset = v.byteOffset;
            this._buffer = v.buffer;
            this._dataView = new DataView(this._buffer, this._byteOffset);
            this._byteLength = this._byteOffset + v.byteLength;
        }
    }], [{
        key: "memcpy",
        value: function memcpy(dst, dstOffset, src, srcOffset, byteLength) {
            var dstU8 = new Uint8Array(dst, dstOffset, byteLength);
            var srcU8 = new Uint8Array(src, srcOffset, byteLength);
            dstU8.set(srcU8);
        }
        /**
         * Converts array to native endianness in-place.
         *
         * @param {Object} array Typed array to convert.
         * @param {boolean} arrayIsLittleEndian True if the data in the array is
         * little-endian. Set false for big-endian.
         * @return {Object} The converted typed array.
         */

    }, {
        key: "arrayToNative",
        value: function arrayToNative(array, arrayIsLittleEndian) {
            if (arrayIsLittleEndian === this.endianness) {
                return array;
            } else {
                return this.flipArrayEndianness(array); // ???
            }
        }
        /**
         * Converts native endianness array to desired endianness in-place.
         *
         * @param {Object} array Typed array to convert.
         * @param {boolean} littleEndian True if the converted array should be
         * little-endian. Set false for big-endian.
         * @return {Object} The converted typed array.
         */

    }, {
        key: "nativeToEndian",
        value: function nativeToEndian(array, littleEndian) {
            if (this.endianness === littleEndian) {
                return array;
            } else {
                return this.flipArrayEndianness(array);
            }
        }
        /**
         * Flips typed array endianness in-place.
         *
         * @param {Object} array Typed array to flip.
         * @return {Object} The converted typed array.
         */

    }, {
        key: "flipArrayEndianness",
        value: function flipArrayEndianness(array) {
            var u8 = new Uint8Array(array.buffer, array.byteOffset, array.byteLength);
            for (var i = 0; i < array.byteLength; i += array.BYTES_PER_ELEMENT) {
                for (
                // tslint:disable-next-line one-variable-per-declaration
                var j = i + array.BYTES_PER_ELEMENT - 1, k = i; j > k; j--, k++) {
                    var tmp = u8[k];
                    u8[k] = u8[j];
                    u8[j] = tmp;
                }
            }
            return array;
        }
        /**
         * Creates an array from an array of character codes.
         * Uses String.fromCharCode in chunks for memory efficiency and then concatenates
         * the resulting string chunks.
         *
         * @param {TypedArray} array Array of character codes.
         * @return {string} String created from the character codes.
         */

    }, {
        key: "createStringFromArray",
        value: function createStringFromArray(array) {
            var chunkSize = 0x8000;
            var chunks = [];
            for (var i = 0; i < array.length; i += chunkSize) {
                chunks.push(String.fromCharCode.apply(null, array.subarray(i, i + chunkSize)));
            }
            return chunks.join("");
        }
    }, {
        key: "defWriteStruct",
        value: function defWriteStruct(readStructDef, struct) {
            var ret = [];
            for (var i = readStructDef.length - 2; i >= 0; i -= 2) {
                var t = readStructDef[i + 1];
                var v = readStructDef[i];
                if (typeof t === "string" && /,.+:[A-Za-z_]/.test(t)) {
                    (function () {
                        var tp = t.split(":");
                        var len = tp[1];
                        tp = tp[0].split(",");
                        t = tp[0];
                        var charset = tp[1];
                        var uint8Array = new _textEncoding.TextEncoder(charset).encode(struct[v]);
                        struct[len] = uint8Array.length;
                        ret.push(function (ds) {
                            return ds.writeUint8Array(uint8Array);
                        });
                    })();
                } else {
                    ret.push(t); // FIXME StructWriteFn is not compatible withi StructReadFn
                }
                ret.push(v);
            }
            return ret.reverse();
        }
    }]);

    return DataStream;
}();
/**
 * Big-endian const to use as default endianness.
 * @type {boolean}
 */


exports.default = DataStream;
DataStream.BIG_ENDIAN = false;
/**
 * Little-endian const to use as default endianness.
 * @type {boolean}
 */
DataStream.LITTLE_ENDIAN = true;
/**
 * Native endianness. Either DataStream.BIG_ENDIAN or DataStream.LITTLE_ENDIAN
 * depending on the platform endianness.
 *
 * @type {boolean}
 */
DataStream.endianness = new Int8Array(new Int16Array([1]).buffer)[0] > 0;
//# sourceMappingURL=index.js.map