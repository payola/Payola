package s2js.adapters.js.browser

class Storage
{
    val length: Long = 0

    def key(index: Long): String = ""

    def getItem(key: String): String = null

    def setItem(key: String, value: String) {}

    def removeItem(key: String) {}

    def clear() {}
}