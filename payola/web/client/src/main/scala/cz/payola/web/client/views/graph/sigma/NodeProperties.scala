package cz.payola.web.client.views.graph.sigma

import s2js.adapters.js.sigma

class NodeProperties extends sigma.NodeProperties {
    var x: Double = math.random
    var y: Double = math.random
    var size: Int = 5
    var color: String = "green"
    var value: Any = null
    var label: String = ""
}
