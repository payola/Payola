package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.Presenter
import scala.collection.mutable.ListBuffer

class Sharing(shareButtonPlaceholderClass: String, entityType: String) extends Presenter
{
    val placeholderList = document.getElementsByClassName(shareButtonPlaceholderClass)
    val shareButtonPresenters = new ListBuffer[ShareButtonPresenter]()


    def initialize() {

    }

    var i = 0
    while (i < placeholderList.length) {
        val placeholder = placeholderList.item(i)
        val id = placeholder.getAttribute("data-shareable-entity-id")
        val public = placeholder.getAttribute("data-shareable-entity-public").toBoolean

        val presenter = new ShareButtonPresenter(placeholder, entityType, id, public)
        shareButtonPresenters += presenter

        i+= 1
    }

}
