package cz.payola.web.client.presenters

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.graph.customization.CustomizationModal
import cz.payola.web.client.events._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.common.ValidationException
import s2js.adapters.js.browser._
import cz.payola.web.client.Presenter

class OntologyCustomizationPresenter(ontologyCustomization: OntologyCustomization) extends Presenter
{

    // This will notify of any value being changed
    val customizationValueChanged: SimpleUnitEvent[this.type] = new SimpleUnitEvent[this.type]

    val modal = new CustomizationModal(ontologyCustomization)


    def classFillColorChangedHandler(args: ClassCustomizationModificationEventArgs[_, String]) {
        OntologyCustomizationManager.setClassFillColor(ontologyCustomization.id, args.classURI, args.value) { Unit =>
            // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.fillColor = args.value
        } { t: Throwable =>
            t match {
                case v: ValidationException => {
                    modal.getFillColorInputForSelectedClass.setState(v ,"fillColor")
                    // TODO reset the value
                }
                case _ => {
                    modal.destroy()
                    fatalErrorHandler(t)
                }
            }
        }
    }

    def classRadiusChangedHandler(args: ClassCustomizationModificationEventArgs[_, Int]) {
        OntologyCustomizationManager.setClassRadius(ontologyCustomization.id, args.classURI, args.value) { Unit =>
        // Success - update the client model
            ontologyCustomization.classCustomizations.find(_.uri == args.classURI).get.radius = args.value
        } { t: Throwable =>
            t match {
                case v: ValidationException => {
                    modal.getRadiusInputForSelectedClass.setState(v ,"radius")
                    // TODO reset the value
                }
                case _ => {
                    modal.destroy()
                    fatalErrorHandler(t)
                }
            }
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
        ontologyCustomization.classCustomizations.find(_.uri == classURI).get.propertyCustomizations.find(_.uri == propertyURI).get
    }

    /** Initialization. Creates a new CustomizationModal and renders it.
      *
      */
    def initialize() {
        modal.classFillColorChanged += classFillColorChangedHandler _
        modal.classRadiusChanged += classRadiusChangedHandler _
        modal.classGlyphChanged += { e =>
            getClassWithURI(e.classURI).glyph = e.value
            postValueChangeNotification()
            true
        }

        modal.classPropertyStrokeColorChanged += { e =>
            getPropertyOfClassWithURIs(e.classURI, e.propertyURI).strokeColor = e.value
            postValueChangeNotification()
            true
        }
        modal.classPropertyStrokeWidthChanged += { e =>
            getPropertyOfClassWithURIs(e.classURI, e.propertyURI).strokeWidth = e.value
            postValueChangeNotification()
            true
        }

        modal.render()
    }

    /** Triggers the customization value changed event.
      *
      */
    private def postValueChangeNotification() {
        customizationValueChanged.trigger(new EventArgs[this.type](this))
    }

}
