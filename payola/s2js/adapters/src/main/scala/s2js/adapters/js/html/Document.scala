package s2js.adapters.js.html

import s2js.adapters.js.Date
import s2js.adapters.js.html.elements.Body
import s2js.adapters.js.dom.NodeList

abstract class Document extends s2js.adapters.js.dom.Document
{
    type ElementType = Element

    val body: Body

    val cookie: String

    val domain: String

    val lastModified: Date

    val readyState: String

    val referrer: String

    var title: String

    val URL: String

    def getElementsByClassName(cssClass: String): NodeList[ElementType]

    def execCommand(command: String, showDefaultUI: Boolean, value: String) {}
}
