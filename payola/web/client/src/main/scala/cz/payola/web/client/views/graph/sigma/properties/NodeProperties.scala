package cz.payola.web.client.views.graph.sigma.properties

import s2js.adapters.js.sigma

class NodeProperties extends sigma.NodeProperties {
    var x: Double = math.random
    var y: Double = math.random
    var size: Int = 5
    var color: String = "green"
    var value: Any = null
    var label: String = ""
    var cluster: Int = 0
}

object NodeProperties {
    val size: Int = 5
    val color: String = "green"
}
