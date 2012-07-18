package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.presenters.components.ShareButton
import s2js.adapters.js.dom.Element
import s2js.compiler.javascript

class Sharing(shareButtonPlaceholderClass: String)
{
    val placeholderList = document.getElementsByClassName(shareButtonPlaceholderClass)

    placeholderList.foreach{ placeholder =>
        val btn = new ShareButton
        btn.render(placeholder)
    }
}
