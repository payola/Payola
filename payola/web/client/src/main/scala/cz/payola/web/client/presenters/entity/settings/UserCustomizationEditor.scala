package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.Presenter
import cz.payola.web.client.events._
import cz.payola.web.client.views.entity.settings.UserCustomizationEditModal
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.models.Model
import cz.payola.common.ValidationException
import cz.payola.web.client.views.bootstrap.modals._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import scala.Some
import cz.payola.common.rdf.Graph

class UserCustomizationEditor (currentGraph: Option[Graph], userCustomization: OntologyCustomization, onClose: () => Unit)
    extends Presenter
{
    val customizationChanged = new SimpleUnitEvent[OntologyCustomizationEventArgs]

    private val view = new UserCustomizationEditModal(currentGraph, userCustomization, onClose)

    def initialize() {
        view.userCustomizationName.delayedChanged += onUserCustomizationNameChanged _
        view.deleteButton.mouseClicked += onDeleteButtonClicked _
        view.classFillColorChanged += onClassFillColorChanged _
        view.classRadiusDelayedChanged += onClassRadiusChanged _
        view.classGlyphChanged += onClassGlyphChanged _
        view.classLabelsChanged += onClassLabelsChanged _
        view.propertyStrokeColorChanged += onPropertyStrokeColorChanged _
        view.propertyStrokeWidthDelayedChanged += onPropertyStrokeWidthChanged _

        view.customizationChanged += { e =>
            customizationChanged.triggerDirectly(new OntologyCustomizationEventArgs(e.target))
        }
        view.render()
    }

    private def onUserCustomizationNameChanged(e: EventArgs[InputControl[_ <: TextInput]]) {
        e.target.isActive = true
        Model.changeOntologyCustomizationName(
            userCustomization, e.target.field.value) { () =>

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
            view.block("Deleting the user customization")
            Model.deleteOntologyCustomization(userCustomization.asInstanceOf[OntologyCustomization]) { () =>
                view.destroy()
                AlertModal.display("Success", "The user customization was successfully deleted.", "alert-success",
                    Some(4000))
            }(fatalErrorHandler(_))
            true
        }

        promptModal.render()
        false
    }

    private def onClassFillColorChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setClassFillColor(userCustomization.id, e.classCustomization.uri, e.newValue) {
            () => successHandler(e, () => e.classCustomization.fillColor = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onClassGlyphChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setClassGlyph(userCustomization.id, e.classCustomization.uri, e.newValue){
            () => successHandler(e, () => e.classCustomization.glyph = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onClassRadiusChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setClassRadius(userCustomization.id, e.classCustomization.uri, e.newValue){
            () => successHandler(e, () => e.classCustomization.radius = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def onClassLabelsChanged(e: ClassCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setClassLabels(userCustomization.id, e.classCustomization.uri, e.newValue){
            () => successHandler(e, () => e.classCustomization.labels = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onPropertyStrokeColorChanged(e: PropertyCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setPropertyStrokeColor(userCustomization.id, e.classCustomization.uri,
            e.propertyCustomization.uri, e.newValue) { () =>
            successHandler(e, () => e.propertyCustomization.strokeColor = e.newValue)
        }(failHandler(_, e.target))
    }

    private def onPropertyStrokeWidthChanged(e: PropertyCustomizationEventArgs[InputControl[_]]) {
        e.target.isActive = true
        OntologyCustomizationManager.setPropertyStrokeWidth(userCustomization.id, e.classCustomization.uri,
            e.propertyCustomization.uri, e.newValue) { () =>
            successHandler(e, () => e.propertyCustomization.strokeWidth = e.newValue.toInt)
        }(failHandler(_, e.target))
    }

    private def successHandler(e: EventArgs[InputControl[_]], valueSetter: () => Unit) {
        e.target.isActive = false
        e.target.setOk()
        valueSetter()
        customizationChanged.triggerDirectly(new OntologyCustomizationEventArgs(userCustomization))
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
