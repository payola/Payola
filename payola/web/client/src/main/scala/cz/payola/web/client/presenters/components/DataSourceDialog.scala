package cz.payola.web.client.presenters.components

import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document

class DataSourceDialog extends Component
{
    private val dialog = new Modal("Find a datasource", List())

    def render(parent: Element = document.body) = {
        dialog.render()
    }

    def getDomElement : Element = {
        dialog.getDomElement()
    }

}
