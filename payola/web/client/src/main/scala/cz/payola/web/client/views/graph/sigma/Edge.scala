package cz.payola.web.client.views.graph.sigma

import s2js.adapters.js.sigma
import cz.payola.domain.entities.settings.OntologyCustomization
import s2js.compiler.javascript

class Edge extends sigma.Edge {

    def updateProperties(newConfiguration: Any) {
        saveCurrentProperties()
        this.color = newConfiguration.asInstanceOf
            [OntologyCustomization#ClassCustomizationType].fillColor //sigma supports only color
    }

    @javascript("self.attr['real_color'] = self.color;")
    def saveCurrentProperties() {}

    @javascript("self.color = self.attr['real_color'];")
    def restartProperties() {}
}
