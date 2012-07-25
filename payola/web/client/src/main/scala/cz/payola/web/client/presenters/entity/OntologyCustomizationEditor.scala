package cz.payola.web.client.presenters.entity

import s2js.adapters.js.browser.window
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.OntologyCustomizationEditModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common.ValidationException
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap.InputControl

class OntologyCustomizationEditor(ontologyCustomization: OntologyCustomization) extends Presenter
{
    protected val saveAsYouTypeTimeout = 1000
    protected var descriptionChangedTimeout: Option[Int] = None

    // This will notify of any value being changed
    val customizationValueChanged: SimpleUnitEvent[this.type] = new SimpleUnitEvent[this.type]

    val modal = new OntologyCustomizationEditModal(ontologyCustomization)

    /** Initialization. Creates a new OntologyCustomizationEditModal and renders it.
      *
      */
    def initialize() {
        modal.classFillColorChanged += classFillColorChangedHandler _
        modal.classRadiusChanged += classRadiusChangedHandler _
        modal.classGlyphChanged += classGlyphChangedHandler _

        modal.propertyStrokeColorChanged += propertyStrokeColorChangedHandler _
        modal.propertyStrokeWidthChanged += propertyStrokeWidthChangedHandler _

        modal.render()
    }

    /** Handler for class fill color change.
      *
      * @param args Args of the event.
      */
    def classFillColorChangedHandler(args: ClassCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        descriptionChangedTimeout.foreach(window.clearTimeout(_))
        descriptionChangedTimeout = Some( window.setTimeout(() => {
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

        descriptionChangedTimeout.foreach(window.clearTimeout(_))
        descriptionChangedTimeout = Some( window.setTimeout(() => {
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

        descriptionChangedTimeout.foreach(window.clearTimeout(_))
        descriptionChangedTimeout = Some( window.setTimeout(() => {
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

    /** Handler for property stroke color change.
      *
      * @param args Args of the event.
      */
    def propertyStrokeColorChangedHandler(args: PropertyCustomizationModificationEventArgs[_]) {
        args.input.setIsActive()

        descriptionChangedTimeout.foreach(window.clearTimeout(_))
        descriptionChangedTimeout = Some( window.setTimeout(() => {
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

        descriptionChangedTimeout.foreach(window.clearTimeout(_))
        descriptionChangedTimeout = Some( window.setTimeout(() => {
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
                modal.destroy()
                fatalErrorHandler(t)
            }
        }
    }
}
