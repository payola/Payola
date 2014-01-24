package cz.payola.domain.rdf

import cz.payola.common.rdf._
import cz.payola.domain.net.Downloader

/**
 * DataCubeVocabulary object servest for obtaining an object representation of the vocabulary based on URL.
 *
 * The apply method takes a URL as a parameter and downloads its contents (TTL). Based on SPARQL queries, it parses the
 * supplied graph and builds up the object representation.
 *
 * @author Jiri Helmich
 */
object DataCubeVocabulary
{
    /**
     * @return An empty definition.
     */
    def empty: DataCubeVocabulary = new DataCubeVocabulary(List(),"")

    private def parseComponent(component: IdentifiedVertex, graph: Graph, componentType: String) = {
        val edges = graph.getOutgoingEdges(component.uri)
        val dimEdge = edges.filter(_.uri == "http://purl.org/linked-data/cube#"+componentType).head
        val orderCollection = edges.filter(_.uri == "http://purl.org/linked-data/cube#order")
        val labelCollection = edges.filter(_.uri == "http://www.w3.org/2000/01/rdf-schema#label")

        val order = if (orderCollection.isEmpty){ None } else {
            orderCollection.head.destination match {
                case x: LiteralVertex => Some(x.value.toString.toInt)
                case _ => None
            }
        }

        val label = if (labelCollection.isEmpty){ None } else {
            labelCollection.head.destination match {
                case x: LiteralVertex => Some(x.value.toString)
                case _ => None
            }
        }

        (dimEdge, label, order)
    }

    /**
     * Creates the object representation of the vocabulary. Searches the supplied RDF-TTL graph for data structure defintions
     * and builds up the representation.
     *
     * @param vocabularyUrl URL containing TTL graph with DCV.
     * @return DCV representation
     */
    def apply(vocabularyUrl: String): DataCubeVocabulary = {

        val vocabularyGraph = JenaGraph(RdfRepresentation.Turtle, new Downloader(vocabularyUrl, "text/rdf+n3").result)

        val definitionsGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { ?d <http://www.w3.org/2000/01/rdf-schema#label> ?l } WHERE { ?d a <http://purl.org/linked-data/cube#DataStructureDefinition>; <http://www.w3.org/2000/01/rdf-schema#label> ?l . }")

        val dataStructureDefinitions = definitionsGraph.edges.map{ e =>

            val dimensionsGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { ?c <http://purl.org/linked-data/cube#dimension> ?x . ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . ?c <http://purl.org/linked-data/cube#order> ?o } WHERE { <"+e.origin.uri+"> <http://purl.org/linked-data/cube#component> ?c . ?c <http://purl.org/linked-data/cube#dimension> ?x . OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . OPTIONAL { ?c <http://purl.org/linked-data/cube#order> ?o . } } }")
            val measuresGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { ?c <http://purl.org/linked-data/cube#measure> ?x . ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . ?c <http://purl.org/linked-data/cube#order> ?o } WHERE { <"+e.origin.uri+"> <http://purl.org/linked-data/cube#component> ?c . ?c <http://purl.org/linked-data/cube#measure> ?x . OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . OPTIONAL { ?c <http://purl.org/linked-data/cube#order> ?o . } } }")
            val attrsGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { ?c <http://purl.org/linked-data/cube#attribute> ?x . ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . ?c <http://purl.org/linked-data/cube#order> ?o } WHERE { <"+e.origin.uri+"> <http://purl.org/linked-data/cube#component> ?c . ?c <http://purl.org/linked-data/cube#attribute> ?x . OPTIONAL { ?c <http://www.w3.org/2000/01/rdf-schema#label> ?l . OPTIONAL { ?c <http://purl.org/linked-data/cube#order> ?o . } } }")

            val dimensions = dimensionsGraph.edges.filter(_.uri == "http://purl.org/linked-data/cube#dimension").map(_.origin).map { component =>
                val data = parseComponent(component, dimensionsGraph, "dimension")
                DataCubeDimension(data._1.destination.toString, data._2, data._3)
            }

            val measures = measuresGraph.edges.filter(_.uri == "http://purl.org/linked-data/cube#measure").map(_.origin).map { component =>
                val data = parseComponent(component, measuresGraph, "measure")
                DataCubeMeasure(data._1.destination.toString, data._2, data._3)
            }

            val attributes = attrsGraph.edges.filter(_.uri == "http://purl.org/linked-data/cube#attribute").map(_.origin).map { component =>
                val data = parseComponent(component, attrsGraph, "attribute")
                DataCubeAttribute(data._1.destination.toString, data._2, data._3)
            }

            if (!dimensions.exists(_.order.isDefined)){
                dimensions.head.order = Some(-1)
            }

            new DataCubeDataStructureDefinition(e.origin.toString, e.destination.toString, dimensions, measures, attributes)
        }

        new DataCubeVocabulary(dataStructureDefinitions, vocabularyUrl)
    }
}
