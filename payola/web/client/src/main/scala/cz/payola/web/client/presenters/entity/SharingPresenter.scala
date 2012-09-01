package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import s2js.adapters.dom
import s2js.adapters.html

/**
 * Presenter which controls the logic of a bunch of ShareButtons. It creates an instance of ShareButtonPresenter for
 * each of the passed placeHolder passed in the first parameter.
 * @param placeHolders List of HTML elements which should contain a sharing button.
 * @param entityType Type of entity which will be shared on button click. {A <: ShareableEntity}
 */
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
