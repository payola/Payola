package cz.payola.web.client.views.graph.sigma.properties

import s2js.adapters.js.sigma

class EdgeProperties extends sigma.EdgeProperties{
    var id = ""
    var source = ""
    var target = ""
    var label = ""
}

object EdgeProperties {
    val color: String = "#EFEFEF"
    val size: Int = 0
    val weight: Int = 0
}
