package cz.payola.domain.rdf

import cz.payola.common.rdf._
import cz.payola.domain.net.Downloader

object DataCubeVocabulary
{
    def empty: DataCubeVocabulary = new DataCubeVocabulary(List(),"")

    def apply(vocabularyUrl: String): DataCubeVocabulary = {

        val vocabularyGraph = Graph(RdfRepresentation.Turtle, new Downloader(vocabularyUrl, "text/rdf+n3").result)

        val definitionsGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { ?d <http://www.w3.org/2000/01/rdf-schema#label> ?l } WHERE { ?d a <http://purl.org/linked-data/cube#DataStructureDefinition>; <http://www.w3.org/2000/01/rdf-schema#label> ?l . }")

        val dataStructureDefinitions = definitionsGraph.edges.map{ e =>

            val dimensionsGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { <"+e.origin.uri+"> <http://payola.cz/data-cube#dimension> ?x } WHERE { <"+e.origin.uri+"> <http://purl.org/linked-data/cube#component> ?c . ?c <http://purl.org/linked-data/cube#dimension> ?x . }")
            val measuresGraph = vocabularyGraph.executeSPARQLQuery("CONSTRUCT { <"+e.origin.uri+"> <http://payola.cz/data-cube#dimension> ?x } WHERE { <"+e.origin.uri+"> <http://purl.org/linked-data/cube#component> ?c . ?c <http://purl.org/linked-data/cube#measure> ?x . }")

            val dimensions = dimensionsGraph.edges.map { dimEdge =>
                DataCubeDimension(dimEdge.destination.toString)
            }

            val measures = measuresGraph.edges.map { measureEdge =>
                DataCubeMeasure(measureEdge.destination.toString)
            }

            new DataCubeDataStructureDefinition(e.origin.toString, e.destination.toString, dimensions, measures)
        }

        new DataCubeVocabulary(dataStructureDefinitions, vocabularyUrl)
    }
}
