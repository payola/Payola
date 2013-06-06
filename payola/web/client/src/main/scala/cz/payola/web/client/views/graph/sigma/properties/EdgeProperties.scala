package cz.payola.web.client.views.graph.sigma.properties

import s2js.adapters.js.sigma

class EdgeProperties extends sigma.EdgeProperties{
    val color = "green"
    var size: Int = 0
    var weight: Int = 0
    //var type: String = ""
    var label: String = ""
    //val attr: List[(String, String)] = null //hidden for javascript only
}
