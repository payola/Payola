package cz.payola.web.client.views.graph.sigma.properties

import s2js.adapters.js.sigma
import cz.payola.common.rdf

class NodeProperties extends sigma.NodeProperties {
    var x: Double = math.random
    var y: Double = math.random
    var size: Int = 5
    var color: String = "#0088cc"
    var value: Any = null
    var label: String = ""
    var id: String = ""
}

object NodeProperties {
    val size: Int = 5
    val color: String = "#0088cc"
}
