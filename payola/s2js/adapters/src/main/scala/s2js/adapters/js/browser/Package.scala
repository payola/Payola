package s2js.adapters.js.browser

import s2js.adapters.js.dom.Document

object `package`
{
    object JSON extends Json

    object window extends Window

    object document extends Document

    val Infinity: Any = null

    val NaN: Any = null

    val undefined: Any = null

    def decodeURI(uri: String): String = ""

    def decodeURIComponent(uri: String): String = ""

    def encodeURI(uri: String): String = ""

    def encodeURIComponent(uri: String): String = ""

    def escape(s: String): String = ""

    def eval(js: String): Any = null

    def isFinite(value: Any): Boolean = false

    def isNaN(value: Any): Boolean = false

    def parseFloat(value: Any): Double = 0.0

    def parseInt(value: Any): Int = 0

    def unescape(s: String): String = ""
}
