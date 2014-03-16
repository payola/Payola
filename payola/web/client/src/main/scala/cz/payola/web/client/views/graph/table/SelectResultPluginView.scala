package cz.payola.web.client.views.graph.table

import scala.collection._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.shared.transformators.IdentityTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

class SelectResultPluginView(prefixApplier: Option[PrefixApplier]) extends TablePluginView("Select Result Table", prefixApplier)
{
    private var variables = mutable.ListBuffer.empty[String]

    private var solutions = mutable.HashMap.empty[String, mutable.ListBuffer[Binding]]

    private var bindings = mutable.HashMap.empty[String, Binding]

    private val csvDownloadButton = new Button(new Text("Export to CSV"), "pull-right", new Icon(Icon.list_alt))

    csvDownloadButton.mouseClicked += { e =>
        var csv = ""

        if (variables.nonEmpty) {
            variables.foreach(csv += _ + ",")
            csv = csv.substring(0, csv.length - 1) + "\n"

            solutions.foreach { s =>
                variables.foreach { variable =>
                    csv += s._2.find(_.variable == variable).map(_.value.toString).getOrElse("") + ","
                }
                csv = csv.substring(0, csv.length - 1) + "\n"
            }
        }


        val modal = new CsvExportModal(csv)
        modal.render()
        false
    }

    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element, tablePageNumber: Int): (Int, Int, Int) = {
        if(graph.isDefined) {
            variables = mutable.ListBuffer.empty[String]
            solutions = mutable.HashMap.empty[String, mutable.ListBuffer[Binding]]
            bindings = mutable.HashMap.empty[String, Binding]

            // Retrieve the bindings.
            graph.get.edges.foreach { e =>
                e.uri match {
                    case u if u.endsWith("#resultVariable") => {
                        variables += e.destination.toString
                    }
                    case u if u.endsWith("#binding") => {
                        val solutionBindings = solutions.getOrElseUpdate(e.origin.uri, mutable.ListBuffer.empty[Binding])
                        val binding = new Binding
                        solutionBindings += binding
                        bindings.put(e.destination.toString, binding)
                    }
                }
            }

            if (variables.isEmpty) {
                tableWrapper.removeAllChildNodes()
                renderMessage(
                    tableWrapper.htmlElement,
                    "The graph isn't a result of a Select SPARQL Query...",
                    "Choose a different visualization plugin."
                )
                0 // nothing to show
            } else {
                // Retrieve the binding values.
                graph.get.edges.foreach { e =>
                    e.uri match {
                        case u if u.endsWith("#value") => {
                            bindings(e.origin.toString).value = e.destination
                        }
                        case u if u.endsWith("#variable") => {
                            bindings(e.origin.toString).variable = e.destination.toString
                        }
                    }
                }

                // Create the headers.
                val headerRow = addRow(tableHead)
                variables.foreach { variable =>
                    val cell = addCell(headerRow, isHeader = true)
                    cell.innerHTML = variable
                }

                // Create the body.
                solutions.foreach { s =>
                    val row = addRow(tableBody)
                    variables.foreach { variable =>
                        val cell = addCell(row)
                        s._2.find(_.variable == variable).map(_.value).foreach {
                            case i: IdentifiedVertex => createVertexView(i).render(cell)
                            case v => new Text(v.toString).render(cell)
                        }
                    }
                }
                1
            }
        }
        ((graph.get.edges.size, 0, 0))
    }

    override def renderControls(toolbar: html.Element) {
        csvDownloadButton.render(toolbar)
    }

    override def destroyControls() {
        csvDownloadButton.destroy()
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

            IdentityTransformator.getSampleGraph(evaluationId) { sample =>
            //TripleTableTransformator.getClass.getName does not work after s2js
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
        IdentityTransformator.transform(evaluationId)(updateGraph(_))
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}

class Binding
{
    var variable: String = ""

    var value: Vertex = null
}
