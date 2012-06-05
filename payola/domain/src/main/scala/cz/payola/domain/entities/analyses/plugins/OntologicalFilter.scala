package cz.payola.domain.entities.analyses.plugins

import scala.collection.immutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.rdf.ontology.Ontology
import java.net.URL
import scala.collection.mutable.ListBuffer
import cz.payola.domain.rdf._

/** This plugin requires one parameter - an ontology URL. The ontology is then
  * loaded and a subgraph that corresponds to the ontology is returned.
  *
  */
class OntologicalFilter(id: String)
    extends Plugin("Ontological Filter", 1, List(new StringParameter("OntologyURL", "")), id)
{
    /** Creates a new instance of a graph that contains only vertices according to
      * the ontology which is described at the OntologyURL URL.
      *
      * @param instance The corresponding instance.
      * @param inputs The input graphs.
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

    /** Creates a new ontology sourced from the OntologyURL parameter.
      *
      * @param instance Plugin instance.
      * @return Output graph.
      */
    private def getOntologyWithPluginInstance(instance: PluginInstance): Ontology = {
        assert(instance.getStringParameter("OntologyURL").isDefined, "OntologyURL parameter must be defined")

        val url = instance.getStringParameter("OntologyURL").get
        val connection = new URL(url).openConnection()
        val requestProperties = Map(
            "Accept" -> "application/rdf+xml"
        )
        requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))

        Ontology(connection.getInputStream)
    }

    /** Takes the graph parameter and filters out all vertices and edges that are not
      * contained in the ontology parameter.
      *
      * @param graph Graph to be filtered.
      * @param ontology Ontology.
      * @return Output graph.
      */
    private def strippedGraphAccordingToOntology(graph: Graph, ontology: Ontology): Graph = {
        val vertices = new ListBuffer[Node]()
        val edges= new ListBuffer[Edge]()

        graph.vertices foreach { n: Node =>
            if (n.isInstanceOf[IdentifiedNode]){
                if (ontology.containsClassWithURI(n.asInstanceOf[IdentifiedNode].uri)){
                    vertices += n
                }
            }
        }

        // Now go through edges
        graph.edges foreach { e: Edge =>
            if (vertices.contains(e.origin)){
                val cl = ontology.getClassForURI(e.origin.uri).get
                if (cl.containsPropertyWithURI(e.uri)){
                    if (e.destination.isInstanceOf[IdentifiedNode] && vertices.contains(e.destination)) {
                        edges += e
                    }else{
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
