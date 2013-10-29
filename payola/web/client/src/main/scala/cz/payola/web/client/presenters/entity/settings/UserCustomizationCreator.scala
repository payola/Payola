package cz.payola.web.client.presenters.entity.settings

import cz.payola.web.client.Presenter
import cz.payola.web.client.events.SimpleUnitEvent
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.settings.UserCustomizationCreateModal
import cz.payola.web.client.models.Model
import cz.payola.common._

class UserCustomizationCreator extends Presenter
{
    val userCustomizationCreated = new SimpleUnitEvent[UserCustomization]

    def initialize() {
        val modal = new UserCustomizationCreateModal
        modal.confirming += { e =>
            modal.block("Creating the user customization.")
            Model.createUserCustomization(modal.name.field.value) { o =>
                modal.unblock()
                modal.destroy()
                userCustomizationCreated.triggerDirectly(o)
            } { error =>
                modal.unblock()
                error match {
                    case v: ValidationException => {
                        modal.name.setState(v, "name")
                    }
                    case _ => {
                        modal.destroy()
                        fatalErrorHandler(error)
                    }
                }
            }
            false
        }

        modal.render()
    }
}