package cz.payola.web.client.views.plugins.visual.settings.components.visualsetup

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.mvvm.element.Div
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.views.plugins.visual.settings.TextSettingsModel
import cz.payola.web.client.mvvm.element.extensions.Bootstrap.Modal

class TextModal(model: TextSettingsModel) extends Component
{
    val settingsChanged = new ComponentEvent[TextModal, EventArgs[TextModal]]

    val colorBackground = new ColorPane("text.color.background", "Text background", model.colorBackground)
    colorBackground.changed += { event =>
        model.colorBackground = colorBackground.getColor
        false
    }

    val color = new ColorPane("text.color.foreground", "Text foreground", model.color)
    color.changed += { event =>
        model.color = color.getColor
        false
    }

    val wrapper = new Div(List(color, colorBackground))

    private val modal = new Modal("Text settings", List(wrapper))

    modal.saved += {
        event => settingsChanged.trigger(new EventArgs[TextModal](this))
    }

    def render(parent: Element = document.body) {
        modal.render(parent)
    }

    def show() {
        modal.show
    }

    def hide() {
        modal.hide
    }

    def getDomElement : Element = modal.getDomElement
}
