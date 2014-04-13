package cz.payola.web.client.views.graph.sigma

import properties._
import cz.payola.common.entities.settings._
import s2js.adapters.js.sigma._
import s2js.compiler.javascript
import s2js.adapters.js.sigma

object PropertiesSetter {

    @javascript(
        """sigmaInstance.graph.nodes().foreach(function(n){
              if(classCustomizations.isEmpty()) {
                n.color = '#0088cc';
              } else {
                  var foundCustomization = classCustomizations.get().find(function(b){return b.uri == n.id;})
                  if(foundCustomization.isDefined()){
                      n.color = foundCustomization.get().fillColor();
                  }
              }
            });""")
    def updateNodes(classCustomizations: Option[List[ClassCustomization]], sigmaInstance: sigma.Sigma) {}
}
