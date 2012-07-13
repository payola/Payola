package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom._
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.plugins.visual.settings.TextSettingsModel
import cz.payola.web.client.views.extensions.bootstrap.Modal
import cz.payola.web.client.events._

class TextModal(model: TextSettingsModel) extends Component
{
    val settingsChanged = new SimpleEvent[TextModal]

    val colorBackground = new ColorPane("text.color.background", "Text background", model.colorBackground)
    colorBackground.changed += { event =>
        model.colorBackground = colorBackground.getColor
    }

    val color = new ColorPane("text.color.foreground", "Text foreground", model.color)
    color.changed += { event =>
        model.color = color.getColor
    }

    val wrapper = new Div(List(color, colorBackground))

    private val modal = new Modal("Text settings", List(wrapper))

    modal.saved += {
        event => settingsChanged.triggerDirectly(this)
    }

    def render(parent: Node) {
        modal.render(parent)
    }

    def show() {
        modal.show
    }

    def hide() {
        modal.hide
    }

    def domElement : Element = modal.domElement
}
