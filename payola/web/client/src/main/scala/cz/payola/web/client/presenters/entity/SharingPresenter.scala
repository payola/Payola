package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import s2js.adapters.js.dom

class SharingPresenter(placeHolders: dom.NodeList[dom.Element], entityType: String) extends Presenter
{
    def initialize() {
        var i = 0
        while (i < placeHolders.length) {
            val placeHolder = placeHolders.item(i)
            val id = placeHolder.getAttribute("data-shareable-entity-id")
            val isPublic = placeHolder.getAttribute("data-shareable-entity-public").toBoolean
            new ShareButtonPresenter(placeHolder, entityType, id, isPublic).initialize()
            i += 1
        }
    }
}
