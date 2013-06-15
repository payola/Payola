package cz.payola.web.client.views.graph.datacube

import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements.Div
import cz.payola.common.rdf.Graph
import cz.payola.web.shared.Geo
import cz.payola.web.client.views.map._
import cz.payola.common.geo.Coordinates
import s2js.compiler.javascript
import cz.payola.web.client.models.PrefixApplier

class TimeHeatmap(prefixApplier: Option[PrefixApplier] = None) extends PluginView("Time heatmap", prefixApplier) {

    val mapPlaceholder = new Div(List(),"map-placeholder")

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

        graph.map { g =>

            val observations = g.getIncomingEdges("http://purl.org/linked-data/cube#Observation").map(_.origin)

            var max = 0

            val triples = observations.map { o =>
                val components = g.getOutgoingEdges(o.uri)

                val place = components.find(_.uri == "http://linked.opendata.cz/resource/czso.cz/dataset-definitions#refArea").map(_.destination)
                val time = components.find(_.uri == "http://linked.opendata.cz/resource/czso.cz/dataset-definitions#refPeriod").map(_.destination)
                val population = components.find(_.uri == "http://linked.opendata.cz/resource/czso.cz/dataset-definitions#finalPopulation").map(_.destination)

                val populationInt = population.map{ p => intval(p.toString) }.getOrElse(0)

                if (populationInt > max){
                    max = populationInt
                }

                (place.getOrElse(""), time.getOrElse("0"), populationInt)
            }


            val places = triples.map{ t =>
                val parts = t._1.toString.split("/")
                parts(parts.length-1).replace("_"," ")
            }

            block("Geocoding places...")
            log(places)
            Geo.geocodeBatch(places) { geo =>
                unblock()
                var i = 0
                val list = geo.map { c =>
                    val r = c.map { coords =>
                        val t = (coords, (triples(i)._3/max*100).toDouble)
                        t
                    }

                    if (!r.isDefined) { log(places(i)) }

                    i = i+1
                    r
                }.filter(_.isDefined).map(_.get)

                                                    log(list.length)

                val center = new Coordinates(0,0)
                val map = new MapView(center, 3, "satellite", list, mapPlaceholder.htmlElement)
                map.render(mapPlaceholder.htmlElement)
            }{ e => }
        }
    }

    def createSubViews = {
        List(mapPlaceholder)
    }
}
