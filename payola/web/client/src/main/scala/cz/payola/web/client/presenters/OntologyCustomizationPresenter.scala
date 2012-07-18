package cz.payola.web.client.presenters

import cz.payola.common.entities.settings._
import cz.payola.web.client.views.graph.customization.CustomizationModal
import s2js.adapters.js.browser._

class OntologyCustomizationPresenter(ontologyCustomization: OntologyCustomization)
{
    var customizationValueHasChangedNotification: Unit => Unit = null

    private def getClassWithURI(uri: String): ClassCustomization = {
        ontologyCustomization.classCustomizations.find(_.uri == uri).get
    }

    private def getPropertyOfClassWithURIs(classURI: String, propertyURI: String): PropertyCustomization = {
        ontologyCustomization.classCustomizations.find(_.uri == classURI).get.propertyCustomizations.find(_.uri == propertyURI).get
    }

    def initialize() {
        val modal = new CustomizationModal(ontologyCustomization)
        modal.classFillColorChanged += { e =>
            getClassWithURI(e.classURI).fillColor = e.value
            postValueChangeNotification
        }
        modal.classRadiusChanged += { e =>
            getClassWithURI(e.classURI).radius = e.value
            postValueChangeNotification
        }
        modal.classGlyphChanged += { e =>
            getClassWithURI(e.classURI).glyph = e.value
            postValueChangeNotification
        }

        modal.classPropertyStrokeColorChanged += { e =>
            getPropertyOfClassWithURIs(e.classURI, e.propertyURI).strokeColor = e.value
            postValueChangeNotification
        }
        modal.classPropertyStrokeWidthChanged += { e =>
            getPropertyOfClassWithURIs(e.classURI, e.propertyURI).strokeWidth = e.value
            postValueChangeNotification
        }

        modal.render()
    }

    private def postValueChangeNotification: Boolean = {
        if (customizationValueHasChangedNotification != null) {
            customizationValueHasChangedNotification()
        }
        true
    }

}
