package cz.payola.web.client.presenters

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.graph.customization.CustomizationModal
import cz.payola.web.client.events._

class OntologyCustomizationPresenter(ontologyCustomization: OntologyCustomization)
{

    // This will notify of any value being changed
    val customizationValueChanged: SimpleUnitEvent[this.type] = new SimpleUnitEvent[this.type]

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
        val modal = new CustomizationModal(ontologyCustomization)
        modal.classFillColorChanged += { e =>
            getClassWithURI(e.classURI).fillColor = e.value
            postValueChangeNotification()
            true
        }
        modal.classRadiusChanged += { e =>
            getClassWithURI(e.classURI).radius = e.value
            postValueChangeNotification()
            true
        }
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
