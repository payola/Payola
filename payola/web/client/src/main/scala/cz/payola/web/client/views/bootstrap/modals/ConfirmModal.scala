package cz.payola.web.client.views.bootstrap.modals

import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.elements._

class ConfirmModal(question: String, details: String, confirmButtonTitle: String, cancelButtonTitle: String = "Cancel",
    isCritical: Boolean = false, additionalCSSClass: String = "")
    extends Modal(question, List(new Text(details)), Some(confirmButtonTitle), Some(cancelButtonTitle), false,
        additionalCSSClass)
{
    override protected val saveButton = new
            Button(new Text(confirmText.getOrElse("")), if (isCritical) "btn-danger" else "btn-primary")
}
