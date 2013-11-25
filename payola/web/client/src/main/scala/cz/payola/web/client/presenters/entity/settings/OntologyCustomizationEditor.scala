package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.settings.OntologyCustomizationEditModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.CustomizationManager
import cz.payola.common.ValidationException
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap.modals._
import cz.payola.web.client.models.Model
import cz.payola.web.client.presenters.entity.ShareButtonPresenter
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.views.bootstrap.InputControl

class OntologyCustomizationEditor(ontologyCustomization: OntologyCustomization) extends Presenter
{
    val customizationValueChanged = new SimpleUnitEvent[this.type]

    private val view = new OntologyCustomizationEditModal(ontologyCustomization)

    private val shareButtonPresenter = new ShareButtonPresenter(
        view.shareButtonViewSpace.htmlElement,
        "Customization", ontologyCustomization.id, ontologyCustomization.name, ontologyCustomization.isPublic,
        Some(view)
    )

    def initialize() {
        shareButtonPresenter.publicityChanged += { e => ontologyCustomization.isPublic = e.target }
        shareButtonPresenter.initialize()

        view.ontologyCustomizationName.delayedChanged += onOntologyCustomizationNameChanged _
        view.deleteButton.mouseClicked += onDeleteButtonClicked _
        view.classFillColorChanged += onClassFillColorChanged _
        view.classRadiusDelayedChanged += onClassRadiusChanged _
        view.classGlyphChanged += onClassGlyphChanged _
        view.propertyStrokeColorChanged += onPropertyStrokeColorChanged _
        view.propertyStrokeWidthDelayedChanged += onPropertyStrokeWidthChanged _

        view.render()
    }

    private def onOntologyCustomizationNameChanged(e: EventArgs[InputControl[_ <: TextInput]]) {
        e.target.isActive = true
        Model.changeCustomizationName(ontologyCustomization, e.target.field.value) { () =>
            e.target.isActive = false
            e.target.setOk()
        } { error =>
            e.target.isActive = false
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

    private def onClassFillColorChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        CustomizationManager.setClassFillColor(ontologyCustomization.id, e.classCustomization.uri,
            e.classCustomization.conditionalValue, e.newValue) {
            () => successHandler(e, () => e.classCustomization.fillColor = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onClassGlyphChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        CustomizationManager.setClassGlyph(ontologyCustomization.id, e.classCustomization.uri,
            e.classCustomization.conditionalValue, e.newValue){
            () => successHandler(e, () => e.classCustomization.glyph = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onClassRadiusChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        CustomizationManager.setClassRadius(ontologyCustomization.id, e.classCustomization.uri,
            e.classCustomization.conditionalValue, e.newValue){
            () => successHandler(e, () => e.classCustomization.radius = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def onPropertyStrokeColorChanged(e: PropertyCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        CustomizationManager.setPropertyStrokeColor(ontologyCustomization.id, e.classCustomization.uri,
            e.classCustomization.conditionalValue, e.propertyCustomization.uri, e.newValue) { () =>
                successHandler(e, () => e.propertyCustomization.strokeColor = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onPropertyStrokeWidthChanged(e: PropertyCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        CustomizationManager.setPropertyStrokeWidth(ontologyCustomization.id, e.classCustomization.uri,
            e.classCustomization.conditionalValue, e.propertyCustomization.uri, e.newValue) { () =>
                successHandler(e, () => e.propertyCustomization.strokeWidth = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def successHandler(e: EventArgs[InputControl[_]], valueSetter: () => Unit) {
        e.target.isActive = false
        e.target.setOk()
        valueSetter()
        customizationValueChanged.triggerDirectly(this)
    }

    /**
     * A fail callback that handles errors during saving of a property.
     * @param t The error.
     * @param input The corresponding input control
     */
    private def failHandler(t: Throwable, input: InputControl[_]) {
        t match {
            case v: ValidationException => {
                input.isActive = false
                input.setError(v.message)
            }
            case _ => {
                view.destroy()
                fatalErrorHandler(t)
            }
        }
    }
}
