package cz.payola.web.client.views.bootstrap.modals

import s2js.adapters.browser._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal

object AlertModal
{
    def display(title: String, description: String, cssClass: String = "", autoCloseDelay: Option[Int] = None) {
        val modal = new AlertModal(title, description, cssClass)

        autoCloseDelay.foreach { d =>
            val timeoutId = window.setTimeout(() => modal.destroy(), d)
            modal.confirming += { e =>
                window.clearTimeout(timeoutId)
                true
            }
        }

        modal.render()
    }
}

class AlertModal(title: String, description: String, alertCssClass: String = "")
    extends Modal(title, List(new Div(List(new Text(description)), "alert " + alertCssClass)), Some("OK"), None, false)
