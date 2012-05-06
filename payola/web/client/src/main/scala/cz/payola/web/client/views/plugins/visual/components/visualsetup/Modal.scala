package cz.payola.web.client.views.plugins.visual.components.visualsetup

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.dom.{Element}
import cz.payola.web.client.events.{EventArgs, ComponentEvent, Event}
import cz.payola.web.client.mvvm_api.element.{Text, Anchor}

/**
 *
 * @author jirihelmich
 * @created 5/5/12 7:43 PM
 * @package cz.payola.web.client.views.plugins.visual.components.visualsetup
 */

class Modal(title: String, body: Seq[Component]) extends Component
{
    def saved = new ComponentEvent[Modal, EventArgs[Modal]]
    def closed = new ComponentEvent[Modal, EventArgs[Modal]]

    val modalDiv = document.createElement[Element]("div")
    modalDiv.setAttribute("class","modal")

    val modalHeader = document.createElement[Element]("div")
    modalHeader.setAttribute("class","modal-header")

    val btnClose = document.createElement[Element]("button")
    btnClose.setAttribute("class","close")
    btnClose.setAttribute("data-dismiss","modal")
    btnClose.innerHTML = "x"

    val heading = document.createElement[Element]("h3")
    heading.innerHTML = title

    val bodyWrap = document.createElement[Element]("div")
    bodyWrap.setAttribute("class","modal-body")

    val footer = document.createElement[Element]("div")
    footer.setAttribute("class","modal-footer")

    val closeA = new Anchor(List(new Text("Close")),"#","btn")
    val saveA = new Anchor(List(new Text("Save changes")),"#","btn btn-primary")

    saveA.clicked += {
        event => saved.trigger(new EventArgs(this))
    }

    closeA.clicked += {
        event => closed.trigger(new EventArgs(this))
    }

    def render(parent: Element = document.body) = {
        modalHeader.appendChild(btnClose)
        modalHeader.appendChild(heading)
        modalDiv.appendChild(modalHeader)
        modalDiv.appendChild(bodyWrap)
        body.map(_.render(bodyWrap))
        modalDiv.appendChild(footer)
        closeA.render(footer)
        saveA.render(footer)

        parent.appendChild(modalDiv)
    }
}
