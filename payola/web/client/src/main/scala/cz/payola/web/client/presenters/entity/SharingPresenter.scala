package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import s2js.adapters.js.dom
import s2js.adapters.js.html
import s2js.adapters.dom.NodeList
import s2js.adapters.html.Element

class SharingPresenter(placeHolders: dom.NodeList[html.Element], entityType: String) extends Presenter
{
    def initialize() {
        var i = 0
        while (i < placeHolders.length) {
            val placeHolder = placeHolders.item(i)
            new ShareButtonPresenter(
                placeHolder,
                entityType,
                placeHolder.getAttribute("data-id"),
                placeHolder.getAttribute("data-name"),
                placeHolder.getAttribute("data-is-public").toBoolean
            ).initialize()
            i += 1
        }
    }
}
