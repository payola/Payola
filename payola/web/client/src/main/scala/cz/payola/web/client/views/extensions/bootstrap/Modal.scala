package cz.payola.web.client.views.extensions.bootstrap

import s2js.compiler.javascript
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.Component
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements.{Text, Anchor}
import cz.payola.web.client.events._

class Modal(title: String, body: Seq[Component], showSave: Boolean = true, showCancel: Boolean = true) extends Component
{
    val saved = new SimpleEvent[Modal]

    val closed = new SimpleEvent[Modal]

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

    saveA.clicked += { e =>
        saved.trigger(this)
        false
    }

    closeA.clicked += { e =>
        closed.trigger(this)
        false
    }

    def render(parent: Element = document.body) {
        modalHeader.appendChild(btnClose)
        modalHeader.appendChild(heading)
        modalDiv.appendChild(modalHeader)
        modalDiv.appendChild(bodyWrap)
        body.map(_.render(bodyWrap))
        modalDiv.appendChild(footer)

        if (showCancel) {
            closeA.render(footer)
        }

        if (showSave) {
            saveA.render(footer)
        }

        parent.appendChild(modalDiv)

        init
    }

    def getDomElement: Element = {
        modalDiv
    }

    @javascript("jQuery(self.modalDiv).modal('show')")
    def show = Nil

    @javascript("jQuery(self.modalDiv).modal('hide')")
    def hide = Nil

    @javascript("$(self.modalDiv).modal({show: false})")
    def init = Nil
}
