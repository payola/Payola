package cz.payola.web.client.presenters.entity

import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.OntologyCustomizationCreateModal
import cz.payola.common.ValidationException
import cz.payola.web.client.models.Model

class OntologyCustomizationCreator extends Presenter
{
    def initialize() {
        val modal = new OntologyCustomizationCreateModal
        modal.confirming += { e =>
            modal.block("Creating the ontology customization.")
            Model.createOntologyCustomization(modal.name.input.value, modal.url.input.value) { o =>
                modal.unblock()
                modal.destroy()
                new OntologyCustomizationEditor(o).initialize()
            } { error =>
                modal.unblock()
                error match {
                    case v: ValidationException => {
                        modal.name.setState(v, "name")
                        modal.url.setState(v, "ontologyURL")
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
