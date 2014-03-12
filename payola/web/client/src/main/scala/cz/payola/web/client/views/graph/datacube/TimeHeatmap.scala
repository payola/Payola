package cz.payola.web.client.views.graph.datacube

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf._
import cz.payola.web.shared.Geo
import cz.payola.web.client.views.map._
import s2js.compiler.javascript
import scala.collection._
import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.map.libwrappers.TimeGoogleHeatMapWrapper
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.web.shared.transformators.IdentityTransformator

/**
 * Time Heatmap visualizer. Based on DCV found in supplied graph, it makes the user able to configure the time dimension,
 * etc.
 *
 * @author Jiri Helmich
 */
class TimeHeatmap(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Graph]("Time heatmap", prefixApplier) {

    val mapPlaceholder = new Div(List(),"map-placeholder")

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0

    var hashMap = new mutable.HashMap[String, mutable.ArrayBuffer[TimeObservation]]
    var max = new mutable.HashMap[String, Int]
    var yearList = new mutable.ArrayBuffer[String]

    var dimensions: List[String] = List()
    var attrUris: List[String]  = List()
    var measureUris: List[String]  = List()

    var timeUri = ""
    var placeUri = ""
    var measureUri = ""

    /**
     * Find DCV definition
     * @param graph The graph to add to the current graph.
     * @param contractLiterals
     */
    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

        graph.map { g =>

            val dataCube = g.edges.filter(_.uri.startsWith("http://purl.org/linked-data/cube#"))

            dimensions = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#dimension").map(_.destination.toString).toList
            measureUris = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#measure").map(_.destination.toString).toList
            attrUris = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#attribute").map(_.destination.toString).toList

            timeUri = dimensions.head
            placeUri = dimensions.tail.head
            measureUri = measureUris.head

            parseGraph(g)
        }
    }

    /**
     * Based on gathered DCV, gather values, build UI, geocode, render
     * @param g
     */
    def parseGraph(g: Graph){

        hashMap = new mutable.HashMap[String, mutable.ArrayBuffer[TimeObservation]]
        max = new mutable.HashMap[String, Int]
        yearList = new mutable.ArrayBuffer[String]

        val observations = g.getIncomingEdges("http://purl.org/linked-data/cube#Observation").map(_.origin)

        val triples = observations.map { o =>
            val components = g.getOutgoingEdges(o.uri)

            val place = components.find(_.uri == placeUri).map(_.destination)
            val time = components.find(_.uri == timeUri).map(_.destination)
            val population = components.find(_.uri == measureUri).map(_.destination)

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

            val rows = (dimensions++attrUris).map { u =>
                val timeicon = new Icon(if (u == timeUri) { Icon.ok }else{ Icon.remove })
                val placeicon = new Icon(if (u == placeUri) { Icon.ok }else{ Icon.remove })

                timeicon.mouseClicked += { e =>
                    timeUri = u
                    placeUri = dimensions.filterNot(_ == u).head
                    parseGraph(g)
                    false
                }

                placeicon.mouseClicked += { e =>
                    placeUri = u
                    timeUri = dimensions.filterNot(_ == u).head
                    parseGraph(g)
                    false
                }

                new TableRow(List(new TableCell(List(new Text(u))),new TableCell(List(timeicon)),new TableCell(List(placeicon)),new TableCell(List(new Text("")))))
            } ++ measureUris.map { u =>
                val measureicon = new Icon(if (u == measureUri) { Icon.ok }else{ Icon.remove })

                measureicon.mouseClicked += { e =>
                    measureUri = u
                    parseGraph(g)
                    false
                }

                new TableRow(List(new TableCell(List(new Text(u))),new TableCell(List(new Text(""))),new TableCell(List(new Text(""))),new TableCell(List(measureicon))))
            }

            val headRow = new TableRow(List(new TableHeadCell(List(new Text("URI"))),new TableHeadCell(List(new Text("Time"))),new TableHeadCell(List(new Text("Place"))),new TableHeadCell(List(new Text("Measure")))))

            val table = new Table(List(new TableHead(List(headRow)),new TableBody(rows)), "table table-striped")

            val settingsBtn = new Button(new Text("DataCube settings"), "", new Icon(Icon.cog))
            val wrap = new Div(List(settingsBtn, table),"heatmap-settings-table")
            var visible = false
            table.hide()

            settingsBtn.mouseClicked += { e =>
                if (visible) {
                    table.hide()
                }else{
                    table.show()
                }
                visible = !visible
                false
            }

            val map = new TimeGoogleHeatMapWrapper(center, 3, "satellite", list, yearList, hashMap, mapPlaceholder.htmlElement)

            mapPlaceholder.removeAllChildNodes()
            wrap.render(mapPlaceholder.htmlElement)
            map.render(mapPlaceholder.htmlElement)
        }{ e => }
    }

    def createSubViews = {
        List(mapPlaceholder)
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        //TODO

        IdentityTransformator.getSampleGraph(evaluationId) { sample =>
            if(sample.isEmpty && availableTransformators.exists(_.contains("IdentityTransformator"))) {
                success()
            } else {
                fail()
            }
        }
        { error =>
            fail()
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Graph] => Unit) {
        //TODO
        IdentityTransformator.transform(evaluationId)(updateGraph(_))
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
