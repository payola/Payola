package cz.payola.web.client.views.graph.visual.settings

import cz.payola.common.entities.settings._
import s2js.adapters.js.browser.window

abstract class SettingsModel {

    private var customization: Option[OntologyCustomization] = None

    def clearOntologySetting() {
        customization = None
    }

    def setOntologyCustomization(newCustomization: Option[OntologyCustomization]) {
        if(newCustomization.isEmpty) {
            clearOntologySetting()
        } else {
            customization = newCustomization
        }
    }

    protected def getCustomization(typeName: String): Option[OntologyCustomization#ClassCustomizationType] = {
        if(customization.isEmpty) {
            None
        } else {
            var comparingTo = ""
            customization.get.classCustomizations.find{custom =>
                comparingTo += custom.uri +"; "
                    custom.uri == typeName
            }
        }
    }

    protected def getProperty(typeName: String, propertyTypeName: String):
    Option[OntologyCustomization#ClassCustomizationType#PropertyCustomizationType] = {

        val customizationType = getCustomization(typeName)
        if(customizationType.isEmpty) {
            None
        } else {
            customizationType.get.propertyCustomizations.find(_.uri == propertyTypeName)
        }
    }
}
