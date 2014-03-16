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
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields._
import s2js.adapters.html.Element
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.shared.transformators.IdentityTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * Generic DataCube visualizer. Based on the graph, it searches DCV definition in it and provides
 * a configurable visualization with the ability to slice a cube.
 *
 * TODO: take advantage of prefixApplier when the component is ready.
 * @param prefixApplier Prefix applier
 * @author Jiri Helmich
 */
class Generic(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Graph]("DataCube Universal", prefixApplier) {

    val controlsHolder = new Div(List(), "col-lg-4 dcv-controlsholder")
    val graphHolder = new Div(List(), "col-lg-8 dcv-graphholder")
    val placeholder = new Div(List(controlsHolder, graphHolder),"dcv-placeholder")
    var dimensions: List[String] = List()
    var labelUri = ""
    var attrUris: List[String]  = List()
    var measureUris: List[String]  = List()
    val datasetUri = "http://purl.org/linked-data/cube#dataSet"
    var filters = new mutable.HashMap[String, Seq[String]]()
    var graphData = new mutable.HashMap[String, Seq[Seq[String]]]
    var isTime = new mutable.HashMap[String, Boolean]
    var pie = false

    def dimensionUris = dimensions.filterNot(_ == labelUri)
    def allkeys = dimensions++attrUris++List(datasetUri)
    def isTimeDimension(uri: String) = isTime.isDefinedAt(uri) && isTime(uri)

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    /**
     * find DCV definition in the supplied graph
     *
     * TODO LDVM input signature
     *
     * @param graph The graph to add to the current graph.
     * @param contractLiterals
     */
    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

        graph.map { g =>

            val dataCube = g.edges.filter(_.uri.startsWith("http://purl.org/linked-data/cube#"))
            dimensions = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#dimension").map(_.destination.toString).toList
            labelUri = dimensions.head
            measureUris = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#measure").map(_.destination.toString).toList
            attrUris = dataCube.filter(_.uri == "http://purl.org/linked-data/cube#attribute").map(_.destination.toString).toList

            parseGraph(g)
        }
    }

    /**
     * Based on detected DCV, it finds values in the graph
     * @param g
     */
    def parseGraph(g: Graph) {

        val dimensionValues = new mutable.HashMap[String,Seq[String]]
        val attrValues = new mutable.HashMap[String,Seq[String]]
        val labelValues = new mutable.HashMap[String,Seq[String]]
        val datasetValues = new mutable.HashMap[String,Seq[String]]

        gatherValues(dimensionUris, dimensionValues, g)
        gatherValues(attrUris, attrValues, g)
        gatherValues(List(datasetUri), datasetValues, g)
        gatherValues(List(labelUri), labelValues, g)

        controlsHolder.removeAllChildNodes()
        graphHolder.removeAllChildNodes()

        val pieButton = new Button(new Text("Pie chart"), "", new Icon(Icon.adjust))
        pieButton.mouseClicked += { e =>
            pie = true
            drawGraph()
            false
        }

        val barButton = new Button(new Text("Bar chart"), "", new Icon(Icon.signal))
        barButton.mouseClicked += { e =>
            pie = false
            drawGraph()
            false
        }

        val btnBar = new Div(List(pieButton, barButton),"well")
        btnBar.render(controlsHolder.blockHtmlElement)

        buildList(dimensionUris, dimensionValues, false, "Dimensions", g).render(controlsHolder.blockHtmlElement)
        buildList(attrUris, attrValues, true, "Attributes", g).render(controlsHolder.blockHtmlElement)
        buildList(List(datasetUri), datasetValues, true, "Datasets", g).render(controlsHolder.blockHtmlElement)
        buildList(List(labelUri), labelValues, true, "Main dimension", g).render(controlsHolder.blockHtmlElement)

        refresh(g)
    }

    /**
     * Uniqe values in order to build controls
     * @param uris values of this predicates
     * @param map key->value map to fill
     * @param graph graph to search in
     */
    def gatherValues(uris: Seq[String], map: mutable.HashMap[String, Seq[String]], graph: Graph){
        uris.map{ u =>
            if (isTimeDimension(u)){
                map.put(u, unique(graph.edges.filter(_.uri == u).map(_.destination.toString.split("-")(0))))
            }else{
                map.put(u, unique(graph.edges.filter(_.uri == u).map(_.destination.toString)))
            }
        }
    }

    /**
     * Build controls
     * @param keys URIs
     * @param source values gathered from the graph
     * @param checkbox checkbox or radio?
     * @param label description
     * @param graph processed graph
     * @return Controls wrapped in div
     */
    def buildList(keys: Seq[String], source: Map[String, Seq[String]],
        checkbox: Boolean, label: String,
        graph: Graph) = {

        new Div(List(new UnorderedList(List(new ListItem(List(new Text(label)),"nav-header"))++keys.map{ u =>

            if (checkbox){
                filters.put(u, source(u))
            }else{
                filters.put(u, source(u).slice(source(u).size-1, source(u).size))
            }

            val item = new UnorderedList(source(u).map{ v =>
                val check = if (checkbox){
                    val cb = new CheckBox(u, true)

                    cb.mouseClicked += { e =>
                        val l = filters(u)
                        if (cb.value){
                            filters.put(u, l ++ List(v))
                        }else{
                            filters.put(u, l.filterNot(_ == v))
                        }
                        refresh(graph)
                        drawGraph()
                        true
                    }
                    cb
                }else{
                    val rad = new Radio(u, true)

                    rad.mouseClicked += { e =>
                        filters.put(u,List(v))
                        refresh(graph)
                        drawGraph()
                        true
                    }
                    rad
                }

                new ListItem(List(check, new Text(v)))
            },"nav nav-list")

            val switch = if (label == "Dimensions"){
                val icon = new Icon(Icon.zoom_in)
                icon.mouseClicked += { e =>
                    labelUri = u
                    parseGraph(graph)
                    drawGraph()
                    false
                }
                icon
            } else {
                new Text(" ")
            }

            val timeSwitch = new Button(new Text(""), "", new Icon(Icon.time))
            timeSwitch.mouseClicked += { e =>
                val was = isTime.isDefinedAt(u) && isTime(u)
                if (was){
                    timeSwitch.removeCssClass("btn-success")
                }else{
                    timeSwitch.addCssClass("btn-success")
                }
                isTime.put(u, !was)
                parseGraph(graph)
                drawGraph()
                false
            }

            new ListItem(List(switch, new Strong(List(new Text(u))), timeSwitch, item))
        }, "nav nav-list")
        ),"well")
    }

    /**
     * Refresh data based on current filters
     * @param graph
     */
    def refresh(graph: Graph) {
        val observations = graph.getIncomingEdges("http://purl.org/linked-data/cube#Observation").map(_.origin)
        val filtered = observations.filterNot { o =>
            val components = graph.getOutgoingEdges(o.uri)
            allkeys.map{ k => // for each dimension, attr, ...
                filters(k).exists { v => // a component has a list of allowed values, dimensions always only 1
                    if (isTimeDimension(k)){
                        components.find(_.uri == k).map(_.destination.toString.split("-")(0)).getOrElse("") == v
                    }else{
                        components.find(_.uri == k).map(_.destination.toString).getOrElse("") == v
                    }
                    // for each allowed value of the dimension try to find a match
                    // only 1 rule => true or false - filter out if false
                    // more rules => match at least one -> exists
                }
                // now we know, whether we should include or exclude the observation based on component
                // if any filter yields false, exclude
            }.exists(_ == false)
        }

        graphData = new mutable.HashMap[String, Seq[Seq[String]]]

        filtered.map{ o =>
            val observationEdges = graph.getOutgoingEdges(o.uri)
            observationEdges.find(_.uri == labelUri).map(_.destination.toString).map{ value =>
                val measureValues = measureUris.map{ mu =>
                    observationEdges.find(_.uri == mu).map(_.destination.toString).getOrElse("")
                }
                if (graphData.isDefinedAt(value)){
                    graphData(value) ++= List(measureValues)
                }else{
                    graphData.put(value, List(measureValues))
                }
            }
        }
    }

    /**
     * Render graph (pie or bar) into the specified element
     * @param element Placeholder
     * @param graphData Data for the graph
     */
    @javascript(
        """
           var measuresCount = self.measureUris.length;
           var element = $(element);

           var data = [];
           for (var i = 0; i < measuresCount; ++i){
            data.push({data: [], label: self.measureUris[i], bars: {'show': 'true', 'align': "center", 'barWidth':0.7}});
           }

          if (!self.pie){
               var counter = 0;
               var ticks = [];
               for (var k in graphData.internalJsObject) { //{k -> [[m1,m2],[m1,m2],[m1,m2],...]}
                var item = graphData.internalJsObject[k]; // [[m1,m2],[m1,m2],[m1,m2],...]
                var acc = {};
               for (var j in item.internalJsArray){
                    var measured = item.internalJsArray[j]; // [m1, m2], [m1, m2], ...
                    for (var m in measured.internalJsArray){
                        acc[m] = acc[m] || 0;
                        acc[m] += parseInt(measured.internalJsArray[m]) || 0;  // m1, m2
                    }
                }
                for (var i = 0; i < measuresCount; ++i){
                    data[i].data.push([counter, acc[i]]);
                }
                ticks.push([counter, k]);
                counter++;
               }
               $.plot(element, data, {
                    xaxes: [{ ticks: ticks }]
               });
           }else{
              var data = [];


             var counter = 0;
             var ticks = [];
             for (var k in graphData.internalJsObject) { //{k -> [[m1,m2],[m1,m2],[m1,m2],...]}
              var item = graphData.internalJsObject[k]; // [[m1,m2],[m1,m2],[m1,m2],...]
              var acc = {};
              for (var j in item.internalJsArray){
                  var measured = item.internalJsArray[j]; // [m1, m2], [m1, m2], ...
                  for (var m in measured.internalJsArray){
                      acc[m] = acc[m] || 0;
                      acc[m] += parseInt(measured.internalJsArray[m]) || 0;  // m1, m2
                  }
              }

              data.push({label: k, data: acc[0]});
             }
              $.plot(element, data, {
                  series: {
                      pie: {
                          show: true
                      }
                  }
              });
           }
        """)
    def flot(element: Element, graphData: Map[String, Seq[Seq[String]]]) {}

    override def drawGraph(){
        graphHolder.removeAllChildNodes()
        flot(graphHolder.blockHtmlElement, graphData)
    }

    /**
     * Unique for JS impl.
     * @return "Set"
     */
    def unique[A](ls: Seq[A]) : Seq[A] = {

        val buff = new mutable.ArrayBuffer[A]()

        ls.foreach{ i =>
            if (!buff.contains(i)){
                buff += i
            }
        }

        buff
    }

    def createSubViews = {
        List(placeholder)
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
        IdentityTransformator.transform(evaluationId)
        { pageOfGraph =>
            updateGraph(pageOfGraph)
        }
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
