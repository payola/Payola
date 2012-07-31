package cz.payola.web.client.views.graph.table

import scala.collection._
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements.Text

class SelectResultPluginView extends TablePluginView("Select Result Table")
{
    def fillTable(graph: Option[Graph], tableHead: Element, tableBody: Element) {
        graph.foreach { g =>
            val variables = mutable.ListBuffer.empty[String]
            val solutions = mutable.HashMap.empty[String, mutable.ListBuffer[Binding]]
            val bindings = mutable.HashMap.empty[String, Binding]

            // Retrieve the bindings.
            g.edges.foreach { e =>
                e.uri match {
                    case "http://www.w3.org/2005/sparql-results#resultVariable" => {
                        variables += e.destination.toString
                    }
                    case "http://www.w3.org/2005/sparql-results#binding" => {
                        val solutionBindings = solutions.getOrElseUpdate(e.origin.uri, mutable.ListBuffer.empty[Binding])
                        val binding = new Binding
                        solutionBindings += binding
                        bindings.put(e.destination.toString, binding)
                    }
                }
            }

            // Retrieve the binding values.
            g.edges.foreach { e =>
                e.uri match {
                    case "http://www.w3.org/2005/sparql-results#value" => {
                        bindings(e.origin.toString).value = e.destination
                    }
                    case "http://www.w3.org/2005/sparql-results#variable" => {
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
        }
    }
}

class Binding
{
    var variable: String = ""

    var value: Vertex = null
}
