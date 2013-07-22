package cz.payola.web.client.views.graph.datacube

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements.Div
import cz.payola.common.rdf._
import cz.payola.web.shared.Geo
import cz.payola.web.client.views.map._
import s2js.compiler.javascript
import scala.collection._
import cz.payola.common.geo.Coordinates

class TimeHeatmap(prefixApplier: Option[PrefixApplier] = None) extends PluginView("Time heatmap", prefixApplier) {

    val mapPlaceholder = new Div(List(),"map-placeholder")

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

        val hashMap = new mutable.HashMap[String, mutable.ArrayBuffer[TimeObservation]]
        val max = new mutable.HashMap[String, Int]
        val yearList = new mutable.ArrayBuffer[String]

        graph.map { g =>

            val observations = g.getIncomingEdges("http://purl.org/linked-data/cube#Observation").map(_.origin)

            val triples = observations.map { o =>
                val components = g.getOutgoingEdges(o.uri)

                val place = components.find(_.uri == "http://datacube.payola.cz/dataset-definitions#location").map(_.destination)
                val time = components.find(_.uri == "http://datacube.payola.cz/dataset-definitions#period").map(_.destination)
                val population = components.find(_.uri == "http://datacube.payola.cz/dataset-definitions#populationSize").map(_.destination)

                val year = if (!time.isDefined){
                    "1900"
                }else{
                    time.get match {
                        case x: LiteralVertex => {
                            val value = x.value.toString.split("-")
                            if (value.length == 3){
                                value(0)
                            }else{
                                "1900"
                            }
                        }
                    }
                }

                val populationInt = population.map{ p => intval(p.toString) }.getOrElse(0)
                if (!max.isDefinedAt(year) || (max.isDefinedAt(year) && max(year) < populationInt)){
                    if (!max.isDefinedAt(year)){
                        yearList += year
                    }
                    max.put(year, populationInt)
                    hashMap.put(year, new mutable.ArrayBuffer[TimeObservation]())
                }

                val tuple = (place.getOrElse(""), year, populationInt)
                tuple
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
                        val t = new TimeObservation(coords, triples(i)._2, triples(i)._3)
                        hashMap(triples(i)._2) += t
                        t
                    }

                    if (!r.isDefined) { log(places(i)) }

                    i = i+1
                    r
                }.filter(_.isDefined).map(_.get)

                val center = new Coordinates(0,0)
                val map = new MapView(center, 3, "satellite", list, yearList, hashMap, mapPlaceholder.htmlElement)
                map.render(mapPlaceholder.htmlElement)
            }{ e => }
        }
    }

    def createSubViews = {
        List(mapPlaceholder)
    }
}
