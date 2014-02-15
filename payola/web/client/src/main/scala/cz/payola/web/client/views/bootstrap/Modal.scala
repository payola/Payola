package cz.payola.web.client.views.bootstrap

import s2js.compiler.javascript
import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events._
import cz.payola.web.client.View

/**
 * A modal popup window.
 * @param header Header of the modal.
 * @param body Components in the modal body.
 * @param confirmText Save button text. If empty, the save button isn't shown.
 * @param cancelText Cancel button text. If empty, the cancel button isn't shown.
 * @param hasCloseButton Whether the close button should be shown.
 */
class Modal(
    val header: String,
    val body: Seq[View] = Nil,
    val confirmText: Option[String] = Some("OK"),
    val cancelText: Option[String] = Some("Cancel"),
    val hasCloseButton: Boolean = true,
    val cssClass: String = "")
    extends ComposedView
{
    /**
     * Triggered when the OK button is clicked. The event handler should return whether the modal should be closed.
     */
    val confirming = new SimpleBooleanEvent[this.type]

    /**
     * Triggered when the cancel or close button is clicked. The event handler should return whether the modal should
     * be closed.
     */
    val closing = new SimpleBooleanEvent[this.type]

    protected val saveButton = new Button(new Text(confirmText.getOrElse("")), "btn-primary")

    protected val cancelButton = new Button(new Text(cancelText.getOrElse("")), "btn-default")

    protected val closeButton = new Button(new Text("x"), "close")

    def createSubViews = {
        saveButton.mouseClicked += { e => buttonClickedHandler(confirming)}
        cancelButton.mouseClicked += { e => buttonClickedHandler(closing)}
        closeButton.mouseClicked += { e => buttonClickedHandler(closing)}

        val modalHeader = new Div(
            (if (hasCloseButton) List(closeButton) else Nil) ++ List(new Heading(List(new Text(header)))),
            "modal-header"
        )

        val modalBody = new Div(
            body,
            "modal-body"
        )

        val modalFooter = new Div(
            (if (cancelText.isDefined) List(cancelButton) else Nil) ++
                (if (confirmText.isDefined) List(saveButton) else Nil),
            "modal-footer"
        )

        val modalContent = new Div(List(modalHeader, modalBody, modalFooter), "modal-content")
        val modalDialog = new Div(List(modalContent), "modal-dialog")
        val modal = new Div(List(modalDialog), "modal fade " + cssClass)
        modal.setAttribute("tabindex","-1")
        modal.setAttribute("role","dialog")
        modal.setAttribute("aria-labelledby","myModalLabel")
        modal.setAttribute("aria-hidden","true")

        List(modal)
    }

    override def render(parent: html.Element = document.body) {
        super.render(parent)
        show()
    }

    override def destroy() {
        hide()
        super.destroy()
    }

    private def buttonClickedHandler(eventToTrigger: SimpleBooleanEvent[this.type]): Boolean = {
        if (eventToTrigger.trigger(new EventArgs[this.type](this))) {
            destroy()
        }
        false
    }

    @javascript("$(self.subViews().head().htmlElement).modal({ show: true, keyboard: false, backdrop: 'static' })")
    private def show() {}

    @javascript("""jQuery(self.subViews().head().htmlElement).modal('hide'); jQuery("body").removeClass('modal-open'); """)
    private def hide() {}
}
