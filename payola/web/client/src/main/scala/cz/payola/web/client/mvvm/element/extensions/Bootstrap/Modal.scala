package cz.payola.web.client.mvvm.element.extensions.Bootstrap

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.dom.Element
import cz.payola.web.client.events.{EventArgs, ComponentEvent}
import cz.payola.web.client.mvvm.element.{Text, Anchor}
import s2js.compiler.javascript
import s2js.adapters.js.browser.window

class Modal(title: String, body: Seq[Component]) extends Component
{
    val saved = new ComponentEvent[Modal, EventArgs[Modal]]

    val closed = new ComponentEvent[Modal, EventArgs[Modal]]

    val modalDiv = document.createElement[Element]("div")

    modalDiv.setAttribute("class", "modal")
    modalDiv.setAttribute("style", "display: none")

    val modalHeader = document.createElement[Element]("div")

    modalHeader.setAttribute("class", "modal-header")

    val btnClose = document.createElement[Element]("button")

    btnClose.setAttribute("class", "close")
    btnClose.setAttribute("data-dismiss", "modal")
    btnClose.innerHTML = "x"

    val heading = document.createElement[Element]("h3")

    heading.innerHTML = title

    val bodyWrap = document.createElement[Element]("div")

    bodyWrap.setAttribute("class", "modal-body")

    val footer = document.createElement[Element]("div")

    footer.setAttribute("class", "modal-footer")

    val closeA = new Anchor(List(new Text("Close")), "#", "btn")

    val saveA = new Anchor(List(new Text("Save changes")), "#", "btn btn-primary")

    saveA.clicked += {
        event =>
            if (saved.trigger(new EventArgs(this))) {
                hide
                true
            } else false
    }

    closeA.clicked += {
        event =>
            if (closed.trigger(new EventArgs(this))) {
                hide
                true
            } else false
    }

    def render(parent: Element = document.body) {
        modalHeader.appendChild(btnClose)
        modalHeader.appendChild(heading)
        modalDiv.appendChild(modalHeader)
        modalDiv.appendChild(bodyWrap)
        body.map(_.render(bodyWrap))
        modalDiv.appendChild(footer)
        closeA.render(footer)
        saveA.render(footer)

        parent.appendChild(modalDiv)

        init
    }

    @javascript("jQuery(self.modalDiv).modal('show')")
    def show = Nil

    @javascript("jQuery(self.modalDiv).modal('hide')")
    def hide = Nil

    @javascript("$(self.modalDiv).modal({show: false})")
    def init = Nil
}
