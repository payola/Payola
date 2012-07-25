package cz.payola.web.client.presenters.entity

import s2js.adapters.js.browser.window
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.OntologyCustomizationEditModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common.exception.ValidationException
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.bootstrap.modals._
import cz.payola.web.client.models.Model
import s2js.runtime.shared.rpc.RpcException
import s2js.adapters.js.browser._

class OntologyCustomizationEditor(ontologyCustomization: OntologyCustomization) extends Presenter
{
    private val saveAsYouTypeTimeout = 1000
    private var fillColorChangedTimeout: Option[Int] = None
    private var strokeColorChangedTimeout: Option[Int] = None
    private var radiusChangedTimeout: Option[Int] = None
    private var glyphChangedTimeout: Option[Int] = None
    private var strokeWidthChangedTimeout: Option[Int] = None
    private var ontologyNameChangeTimeout: Option[Int] = None

    // This will notify of any value being changed
    val customizationValueChanged: SimpleUnitEvent[this.type] = new SimpleUnitEvent[this.type]

    val view = new OntologyCustomizationEditModal(ontologyCustomization)

    val shareButtonPresenter = new ShareButtonPresenter(view.shareButtonViewSpace.domElement, "customization",
        ontologyCustomization.id, ontologyCustomization.isPublic, Some(view))

    /** Initialization. Creates a new OntologyCustomizationEditModal and renders it.
          *
          */
    def initialize() {
        shareButtonPresenter.initialize()

        view.classFillColorChanged += classFillColorChangedHandler _
        view.classRadiusChanged += classRadiusChangedHandler _
        view.classGlyphChanged += classGlyphChangedHandler _
        view.propertyStrokeColorChanged += propertyStrokeColorChangedHandler _
        view.propertyStrokeWidthChanged += propertyStrokeWidthChangedHandler _
        view.deleteButton.mouseClicked += deleteCustomizationHandler _

        view.ontologyNameChanged += ontologyNameChangedHandler _

        view.render()
    }

    /** Handler for class fill color change.
      *
      * @param args Args of the event.
      */
    def classFillColorChangedHandler(args: ClassCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        fillColorChangedTimeout.foreach(window.clearTimeout(_))
        fillColorChangedTimeout = Some( window.setTimeout(() => {
            OntologyCustomizationManager.setClassFillColor(ontologyCustomization.id, args.classURI, args.value)
            { () =>
                // Success - set field OK and update the client model
                args.input.setIsActive(false)
                args.input.setOk()

                getClassWithURI(args.classURI).fillColor = args.value
                postValueChangeNotification()
            } { t: Throwable => failHandler(t, args.input) }
        }, saveAsYouTypeTimeout))
    }

    /** Handler for class glyph change.
      *
      * @param args Args of the event.
      */
    def classGlyphChangedHandler(args: ClassCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        glyphChangedTimeout.foreach(window.clearTimeout(_))
        glyphChangedTimeout = Some( window.setTimeout(() => {
            OntologyCustomizationManager.setClassGlyph(ontologyCustomization.id, args.classURI, args.value)
            { () =>
                // Success - set field OK and update the client model
                args.input.setIsActive(false)
                args.input.setOk()

                val glyph = if (args.value == "") None else Some(args.value(0))
                getClassWithURI(args.classURI).glyph = glyph
                postValueChangeNotification()
            } { t: Throwable => failHandler(t, args.input) }
        }, saveAsYouTypeTimeout))
    }

    /** Handler for class radius change.
      *
      * @param args Args of the event.
      */
    def classRadiusChangedHandler(args: ClassCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        radiusChangedTimeout.foreach(window.clearTimeout(_))
        radiusChangedTimeout = Some( window.setTimeout(() => {
            OntologyCustomizationManager.setClassRadius(ontologyCustomization.id, args.classURI, args.value)
            { () =>
                // Success - set field OK and update the client model
                args.input.setIsActive(false)
                args.input.setOk()

                getClassWithURI(args.classURI).radius = args.value.toInt
                postValueChangeNotification()
            } { t: Throwable => failHandler(t, args.input) }
        }, saveAsYouTypeTimeout))
    }

    private def deleteCustomizationHandler(e: EventArgs[_]) = {
        val promptModal = new ConfirmModal("Do you really want to delete this customization?", "This action cannot be undone.", "Delete", "Cancel", true, "alert-error")
        promptModal.confirming += { e =>
            Model.deleteOntologyCustomization(ontologyCustomization) { () =>
                view.destroy()
                AlertModal.runModal("Ontology customization successfully deleted.", "Success!", "alert-success")
            }{ error =>
                error match {
                    case exc: RpcException => AlertModal.runModal(exc.message, "Error removing ontology customization.", "alert-error")
                    case _ => {
                        view.destroy()
                        fatalErrorHandler(error)
                    }
                }
            }
            true
        }
        promptModal.render()
        true
    }

    /** Handler for property stroke color change.
      *
      * @param args Args of the event.
      */
    def propertyStrokeColorChangedHandler(args: PropertyCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        strokeColorChangedTimeout.foreach(window.clearTimeout(_))
        strokeColorChangedTimeout = Some( window.setTimeout(() => {
            OntologyCustomizationManager
                .setPropertyStrokeColor(ontologyCustomization.id, args.classURI, args.propertyURI, args.value)
            { () =>
                // Success - set field OK and update the client model
                args.input.setIsActive(false)
                args.input.setOk()

                getPropertyOfClassWithURIs(args.classURI, args.propertyURI).strokeColor = args.value
                postValueChangeNotification()
            } { t: Throwable => failHandler(t, args.input) }
        }, saveAsYouTypeTimeout))
    }

    /** Handler for property stroke color change.
      *
      * @param args Args of the event.
      */
    def propertyStrokeWidthChangedHandler(args: PropertyCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        strokeWidthChangedTimeout.foreach(window.clearTimeout(_))
        strokeWidthChangedTimeout = Some( window.setTimeout(() => {
            OntologyCustomizationManager.setPropertyStrokeWidth(
                ontologyCustomization.id, args.classURI, args.propertyURI, args.value)
            { () =>
                // Success - set field OK and update the client model
                args.input.setIsActive(false)
                args.input.setOk()

                getPropertyOfClassWithURIs(args.classURI, args.propertyURI).strokeWidth = args.value.toInt
                postValueChangeNotification()
            } { t: Throwable => failHandler(t, args.input) }
        }, saveAsYouTypeTimeout))
    }

    /** Retrieves a class customization for a class URI from the ontology customization.
      *
      * @param uri Class URI.
      * @return Class customization.
      */
    private def getClassWithURI(uri: String): ClassCustomization = {
        ontologyCustomization.classCustomizations.find(_.uri == uri).get
    }

    /** Retrieves a property customization for a class and property URIs from the ontology customization.
      *
      * @param classURI Class URI.
      * @param propertyURI Property URI.
      * @return Property customization.
      */
    private def getPropertyOfClassWithURIs(classURI: String, propertyURI: String): PropertyCustomization = {
        getClassWithURI(classURI).propertyCustomizations.find(_.uri == propertyURI).get
    }

    /** Triggers the customization value changed event.
      *
      */
    private def postValueChangeNotification() {
        customizationValueChanged.trigger(new EventArgs[this.type](this))
    }

    private def renameOntology(inputControl: InputControl){
        Model.changeOntologyCustomizationName(ontologyCustomization, inputControl.input.value) { () =>
            inputControl.setIsActive(false)
            inputControl.setOk()
        } { error: Throwable =>
            inputControl.setIsActive(false)
            error match {
                case exc: ValidationException => inputControl.setState(exc, "name")
                case exc: RpcException => AlertModal.runModal(exc.message)
                case _ => fatalErrorHandler(error)
            }
        }
    }

    private def ontologyNameChangedHandler(evArgs: EventArgs[_]) {
        val inputControl = view.customizationNameField
        inputControl.setIsActive(true)

        ontologyNameChangeTimeout.foreach(window.clearTimeout(_))
        ontologyNameChangeTimeout = Some(delayed(1000) { () =>
            renameOntology(inputControl)
        })
    }

    /** Failure handler for property saving.
      *
      * @param t The error.
      * @param input InputControl which caused failure
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
