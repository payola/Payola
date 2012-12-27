package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.ValidationException
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.settings.OntologyCustomizationCreateModal
import cz.payola.web.client.models.Model
import cz.payola.web.client.events.SimpleUnitEvent

class OntologyCustomizationCreator extends Presenter
{
    val ontologyCustomizationCreated = new SimpleUnitEvent[OntologyCustomization]

    def initialize() {
        val modal = new OntologyCustomizationCreateModal
        modal.confirming += { e =>
            modal.block("Creating the ontology customization.")
            Model.createOntologyCustomization(modal.name.field.value, modal.urls.field.value) { o =>
                modal.unblock()
                modal.destroy()
                ontologyCustomizationCreated.triggerDirectly(o)
            } { error =>
                modal.unblock()
                error match {
                    case v: ValidationException => {
                        modal.name.setState(v, "name")
                        modal.urls.setState(v, "ontologyURLs")
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
