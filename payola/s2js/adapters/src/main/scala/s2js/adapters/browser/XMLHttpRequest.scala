package s2js.adapters.browser

import s2js.adapters.events._
import s2js.adapters.dom.Document

class XMLHttpRequest extends EventTarget
{
    val readyState: Int = 0

    val status: Int = 0

    val statusText: String = ""

    val response: Any = ""

    val responseText: String = ""

    val responseXML: Document = null

    var onloadstart: Event[this.type] => Unit = null

    var onprogress: Event[this.type] => Unit = null

    var onabort: Event[this.type] => Unit = null

    var onerror: Event[this.type] => Unit = null

    var onload: Event[this.type] => Unit = null

    var ontimeout: Event[this.type] => Unit = null

    var onloadend: Event[this.type] => Unit = null

    var onreadystatechange: Event[this.type] => Unit = null

    def open(method: String, url: String, async: Boolean = true, username: String = "", password: String = "") {}

    def setRequestHeader(header: String, value: String) {}

    def send(data: String = "") {}

    def abort() {}

    def getResponseHeader(header: String): String = ""
}
