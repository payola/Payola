package cz.payola.domain.entities.plugins.concrete

import collection.immutable
import collection.mutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf.Graph
import cz.payola.domain.rdf.ontology.Ontology
import cz.payola.domain.net.Downloader
import cz.payola.common.rdf._
import cz.payola.common.rdf.ontology._
import cz.payola.domain.sparql._
import scala.collection.mutable.ListBuffer
import cz.payola.domain.sparql._

/** This plugin requires one parameter - an ontology URL. The ontology is then
  * loaded and a subgraph that corresponds to the ontology is returned.
  *
  */
class OntologicalFilter(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    def this() = {
        this("Ontological Filter", 1, List(new StringParameter("OntologyURLs", "")), IDGenerator.newId)
        isPublic = true
    }

    /** Creates a new SPARQL query that filters the graph according to the ontology.
      *
      * @param ontology The ontology.
      * @return SPARQL query.
      */
    private def getOntologyFilteringSPARQLQuery(ontology: Ontology): ConstructQuery = {
        val template = new ListBuffer[TriplePattern]()
        val classPatterns = new ListBuffer[TriplePattern]()
        val variablePatterns = new ListBuffer[GraphPattern]()

        var xCounter = 1
        var vCounter = 1

        ontology.classes foreach { case (_, cl) =>
            val variable = new Variable("x" + xCounter)
            xCounter = xCounter + 1

            val classTP = new TriplePattern(variable, Uri(Edge.rdfTypeEdge), new Uri(cl.uri))
            template += classTP
            classPatterns += classTP

            cl.properties foreach { case (_, prop) =>
                val propVariable = new Variable("v" + vCounter)
                vCounter = vCounter + 1

                val variableTP = new TriplePattern(variable, new Uri(prop.uri), propVariable)
                template += variableTP
                variablePatterns += new GraphPattern(List(variableTP))
            }
        }

        val patternGP = new GraphPattern(classPatterns.toList, variablePatterns.toList)
        val query = new ConstructQuery(template.toList, Some(patternGP))
        query
    }

    /** See superclass.
      *
      * @param instance The evaluated plugin instance.
      * @return The query.
      */
    override def getQuery(instance: PluginInstance): String = {
        val ontology = getOntologyWithPluginInstance(instance)

        assert(ontology != null, "Ontology couldn't be created")

        getOntologyFilteringSPARQLQuery(ontology).toString
    }

    /** Creates a new ontology sourced from the OntologyURLs parameter.
      *
      * @param instance Plugin instance.
      * @return Output graph.
      */
    private def getOntologyWithPluginInstance(instance: PluginInstance): Ontology = {
        usingDefined(instance.getStringParameter("OntologyURLs")) { (ontologyURLs: String) =>
            // Assume that there can be more ontology urls separated by a newline.
            val urls = ontologyURLs.split("\n").toList.filter(!_.isEmpty)

            // Download and merge all ontologies in parallel.
            val ontologies = urls.par.map(url => Ontology(new Downloader(url, accept = "application/rdf+xml").result))
            ontologies.fold(Ontology.empty)(_ + _)
        }
    }

}
