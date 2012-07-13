package cz.payola.web.client.presenters.components

import cz.payola.web.client.views.Component
import s2js.adapters.js.dom._
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.extensions.bootstrap.Modal

class DataSourceDialog extends Component
{
    private val dialog = new Modal("Find a datasource", List())

    def render(parent: Node) {
        dialog.render()
    }

    def domElement : Element = {
        dialog.domElement
    }

    def destroy() {
        // TODO
    }

}
