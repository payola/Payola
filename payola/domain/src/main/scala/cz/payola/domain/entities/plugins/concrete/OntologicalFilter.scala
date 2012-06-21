package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import collection.mutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import cz.payola.domain.rdf.ontology.Ontology

/** This plugin requires one parameter - an ontology URL. The ontology is then
  * loaded and a subgraph that corresponds to the ontology is returned.
  *
  */
class OntologicalFilter(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def this() = this("Ontological Filter", 1, List(new StringParameter("OntologyURLs", "")), IDGenerator.newId)

    /** Creates a new instance of a graph that contains only vertices according to
      * the ontology which is described at the OntologyURLs URL.
      *
      * @param instance The corresponding instance.
      * @param inputs ThOntologicalFiltere input graphs.
      * @param progressReporter A method that can be used to report plugin evaluation progress (which has to be within
      *                         the (0.0, 1.0] interval).
      * @return The output graph.
      */
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        val definedInputs = getDefinedInputs(inputs)
        val ontology = getOntologyWithPluginInstance(instance)

        assert(definedInputs.size > 0, "This plugin requires some input!")
        assert(ontology != null, "Ontology couldn't be created")

        strippedGraphAccordingToOntology(definedInputs(0), ontology)
    }

    private def getOntologyAtURL(url: String): Ontology = {
        val connection = new java.net.URL(url).openConnection()
        val requestProperties = Map(
            "Accept" -> "application/rdf+xml"
        )
        requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))

        Ontology(connection.getInputStream)
    }

    /** Creates a new ontology sourced from the OntologyURLs parameter.
      *
      * @param instance Plugin instance.
      * @return Output graph.
      */
    private def getOntologyWithPluginInstance(instance: PluginInstance): Ontology = {
        assert(instance.getStringParameter("OntologyURLs").isDefined, "OntologyURLs parameter must be defined")

        // Assume that there can be more ontologies separated by a newline
        val urlList = instance.getStringParameter("OntologyURLs").get
        val URLs = urlList.split("\n").filter { p: String => !p.isEmpty}

        assert(URLs.length != 0, "No URLs defined (empty string, or only whitespace!")

        var resultingOntology = getOntologyAtURL(URLs(0))

        for (i <- 1 until URLs.length) {
            resultingOntology = resultingOntology + getOntologyAtURL(URLs(i))
        }

        resultingOntology
    }

    /** Takes the graph parameter and filters out all vertices and edges that are not
      * contained in the ontology parameter.
      *
      * @param graph Graph to be filtered.
      * @param ontology Ontology.
      * @return Output graph.
      */
    private def strippedGraphAccordingToOntology(graph: Graph, ontology: Ontology): Graph = {
        val vertices = new mutable.ListBuffer[Node]()
        val edges = new mutable.ListBuffer[Edge]()

        graph.vertices foreach { n: Node =>
            if (n.isInstanceOf[IdentifiedNode]) {
                if (ontology.containsClassWithURI(n.asInstanceOf[IdentifiedNode].uri)) {
                    vertices += n
                }
            }
        }

        // Now go through edges
        graph.edges foreach { e: Edge =>
            if (vertices.contains(e.origin)) {
                val cl = ontology.getClassForURI(e.origin.uri).get
                if (cl.containsPropertyWithURI(e.uri)) {
                    if (e.destination.isInstanceOf[IdentifiedNode] && vertices.contains(e.destination)) {
                        edges += e
                    } else {
                        // Literal -> add it to vertices and add the edge
                        vertices += e.destination
                        edges += e
                    }
                }
            }
        }

        new Graph(vertices, edges)
    }
}
