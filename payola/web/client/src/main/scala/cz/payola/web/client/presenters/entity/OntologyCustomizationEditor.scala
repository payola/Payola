package cz.payola.web.client.presenters.entity

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.entity.OntologyCustomizationEditModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common.ValidationException
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.bootstrap.InputControl
import cz.payola.web.client.views.bootstrap.modals._
import cz.payola.web.client.models.Model
import s2js.runtime.shared.rpc.RpcException

class OntologyCustomizationEditor(ontologyCustomization: OntologyCustomization) extends Presenter
{
    // This will notify of any value being changed
    val customizationValueChanged: SimpleUnitEvent[this.type] = new SimpleUnitEvent[this.type]

    val view = new OntologyCustomizationEditModal(ontologyCustomization)

    val shareButtonPresenter = new ShareButtonPresenter(view.shareButtonDiv, )

    /** Failure handler for property saving.
      *
      * @param t The error.
      * @param inputFetcher A function which fetches the input control from which the property has been edited.
      * @param valueName Name of the value.
      */
    def classValueSetterFailHandler(t: Throwable, inputFetcher: => InputControl, valueName: String) {
        t match {
            case v: ValidationException => {
                inputFetcher.setState(v, valueName)
                // TODO reset the value
            }
            case _ => {
                view.destroy()
                fatalErrorHandler(t)
            }
        }
    }

    /** Handler for class fill color change.
      *
      * @param args Args of the event.
      */
    def classFillColorChangedHandler(args: ClassCustomizationModificationEventArgs[_, String]) {
        OntologyCustomizationManager.setClassFillColor(ontologyCustomization.id, args.classURI, args.value) { () =>
            // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.fillColor = args.value
            postValueChangeNotification()
        } { t: Throwable =>
            classValueSetterFailHandler(t, {
                view.getFillColorInputForSelectedClass
            }, "fillColor")
        }
    }

    /** Handler for class glyph change.
      *
      * @param args Args of the event.
      */
    def classGlyphChangedHandler(args: ClassCustomizationModificationEventArgs[_, Option[Char]]) {
        OntologyCustomizationManager.setClassGlyph(ontologyCustomization.id, args.classURI, args.value) { () =>
        // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.glyph = args.value
            postValueChangeNotification()
        } { t: Throwable =>
            classValueSetterFailHandler(t, {
                view.getGlyphInputForSelectedClass
            }, "glyph")
        }
    }

    /** Handler for class radius change.
      *
      * @param args Args of the event.
      */
    def classRadiusChangedHandler(args: ClassCustomizationModificationEventArgs[_, Int]) {
        OntologyCustomizationManager.setClassRadius(ontologyCustomization.id, args.classURI, args.value) { () =>
        // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.radius = args.value
            postValueChangeNotification()
        } { t: Throwable =>
            classValueSetterFailHandler(t, {
                view.getRadiusInputForSelectedClass
            }, "radius")
        }
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
    def propertyStrokeColorChangedHandler(args: ClassPropertyCustomizationModificationEventArgs[_, String]) {
        OntologyCustomizationManager
            .setPropertyStrokeColor(ontologyCustomization.id, args.classURI, args.propertyURI, args.value) { () =>
        // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.propertyCustomizations
                .find(_.uri == args.propertyURI).get.strokeColor = args.value
            postValueChangeNotification()
        } { t: Throwable =>
            classValueSetterFailHandler(t, {
                view.getStrokeColorInputForPropertyOfSelectedClass(args.propertyURI)
            }, "strokeColor")
        }
    }

    /** Handler for property stroke color change.
      *
      * @param args Args of the event.
      */
    def propertyStrokeWidthChangedHandler(args: ClassPropertyCustomizationModificationEventArgs[_, Int]) {
        OntologyCustomizationManager
            .setPropertyStrokeWidth(ontologyCustomization.id, args.classURI, args.propertyURI, args.value) { () =>
        // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.propertyCustomizations
                .find(_.uri == args.propertyURI).get.strokeWidth = args.value
            postValueChangeNotification()
        } { t: Throwable =>
            classValueSetterFailHandler(t, {
                view.getStrokeWidthInputForPropertyOfSelectedClass(args.propertyURI)
            }, "strokeWidth")
        }
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
        ontologyCustomization.classCustomizations.find(_.uri == classURI).get.propertyCustomizations
            .find(_.uri == propertyURI).get
    }

    /** Initialization. Creates a new OntologyCustomizationEditModal and renders it.
      *
      */
    def initialize() {
        view.classFillColorChanged += classFillColorChangedHandler _
        view.classRadiusChanged += classRadiusChangedHandler _
        view.classGlyphChanged += classGlyphChangedHandler _

        view.classPropertyStrokeColorChanged += propertyStrokeColorChangedHandler _
        view.classPropertyStrokeWidthChanged += propertyStrokeWidthChangedHandler _

        view.deleteButton.mouseClicked += deleteCustomizationHandler _

        view.render()
    }

    /** Triggers the customization value changed event.
      *
      */
    private def postValueChangeNotification() {
        customizationValueChanged.trigger(new EventArgs[this.type](this))
    }
}
