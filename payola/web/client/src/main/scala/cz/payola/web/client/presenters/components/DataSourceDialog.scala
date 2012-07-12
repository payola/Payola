package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.Component
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.extensions.bootstrap.Modal

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
