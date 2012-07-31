package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.settings.OntologyCustomizationEditModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common.ValidationException
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.bootstrap.modals._
import cz.payola.web.client.models.Model
import cz.payola.web.client.presenters.entity.ShareButtonPresenter
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.ColorPane

class OntologyCustomizationEditor(ontologyCustomization: OntologyCustomization) extends Presenter
{
    val customizationValueChanged = new SimpleUnitEvent[this.type]

    private val view = new OntologyCustomizationEditModal(ontologyCustomization)

    private val shareButtonPresenter = ShareButtonPresenter(
        view.shareButtonViewSpace.domElement,
        ontologyCustomization,
        Some(view)
    )

    def initialize() {
        shareButtonPresenter.initialize()

        view.ontologyCustomizationName.delayedChanged += onOntologyCustomizationNameChanged _
        view.deleteButton.mouseClicked += onDeleteButtonClicked _
        view.classFillColorChanged += onClassFillColorChanged _
        view.classRadiusDelayedChanged += onClassRadiusChanged _
        view.classGlyphDelayedChanged += onClassGlyphChanged _
        view.propertyStrokeColorChanged += onPropertyStrokeColorChanged _
        view.propertyStrokeWidthDelayedChanged += onPropertyStrokeWidthChanged _

        view.render()
    }

    private def onOntologyCustomizationNameChanged(e: EventArgs[InputControl]) {
        e.target.setIsActive(true)
        Model.changeOntologyCustomizationName(ontologyCustomization, e.target.input.value) { () =>
            e.target.setIsActive(false)
            e.target.setOk()
        } { error =>
            e.target.setIsActive(false)
            error match {
                case v: ValidationException => e.target.setState(v, "name")
                case _ => fatalErrorHandler(error)
            }
        }
    }

    private def onDeleteButtonClicked(e: EventArgs[_]) = {
        val promptModal = new ConfirmModal("Do you really want to delete this customization?", 
            "This action cannot be undone.", "Delete", "Cancel", true, "alert-error")
        
        promptModal.confirming += { e =>
            view.block("Deleting the ontology customization")
            Model.deleteOntologyCustomization(ontologyCustomization) { () =>
                view.destroy()
                AlertModal.display("Success", "The ontology customization was successfully deleted.", "alert-success",
                    Some(4000))
            }(fatalErrorHandler(_))
            true
        }
        
        promptModal.render()
        false
    }

    private def onClassFillColorChanged(e: ClassCustomizationEventArgs[ColorPane]) {
        // TODO e.target.setIsActive(true)
        OntologyCustomizationManager.setClassFillColor(ontologyCustomization.id, e.classCustomization.uri, e.newValue) {
            () => // TODO successHandler(e, () => e.classCustomization.fillColor = e.newValue)
                e.classCustomization.fillColor = e.newValue
                customizationValueChanged.triggerDirectly(this)
        }(/* TODO failHandler(_, e.target)*/ fatalErrorHandler(_))
    }

    private def onClassGlyphChanged(e: ClassCustomizationEventArgs[InputControl]) {
        e.target.setIsActive(true)
        OntologyCustomizationManager.setClassGlyph(ontologyCustomization.id, e.classCustomization.uri, e.newValue){
            () => successHandler(e, () => e.classCustomization.glyph = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onClassRadiusChanged(e: ClassCustomizationEventArgs[InputControl]) {
        e.target.setIsActive(true)
        OntologyCustomizationManager.setClassRadius(ontologyCustomization.id, e.classCustomization.uri, e.newValue){
            () => successHandler(e, () => e.classCustomization.radius = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def onPropertyStrokeColorChanged(e: PropertyCustomizationEventArgs[ColorPane]) {
        // TODO e.target.setIsActive(true)
        OntologyCustomizationManager.setPropertyStrokeColor(ontologyCustomization.id, e.classCustomization.uri, 
            e.propertyCustomization.uri, e.newValue) { () =>

            // TODO successHandler(e, () => e.propertyCustomization.strokeColor = e.newValue)
            e.propertyCustomization.strokeColor = e.newValue
            customizationValueChanged.triggerDirectly(this)
        }(/* TODO failHandler(_, e.target)*/ fatalErrorHandler(_))
    }

    private def onPropertyStrokeWidthChanged(e: PropertyCustomizationEventArgs[InputControl]) {
        e.target.setIsActive(true)
        OntologyCustomizationManager.setPropertyStrokeWidth(ontologyCustomization.id, e.classCustomization.uri,
            e.propertyCustomization.uri, e.newValue) { () =>

            successHandler(e, () => e.propertyCustomization.strokeWidth = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def successHandler(e: EventArgs[InputControl], valueSetter: () => Unit) {
        e.target.setIsActive(false)
        e.target.setOk()
        valueSetter()
        customizationValueChanged.triggerDirectly(this)
    }

    /**
     * A fail callback that handles errors during saving of a property.
     * @param t The error.
     * @param input The corresponding input control
     */
    private def failHandler(t: Throwable, input: InputControl) {
        t match {
            case v: ValidationException => {
                input.setIsActive(false)
                input.setError(v.message)
            }
            case _ => {
                view.destroy()
                fatalErrorHandler(t)
            }
        }
    }
}
