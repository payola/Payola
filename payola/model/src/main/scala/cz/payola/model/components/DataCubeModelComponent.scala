package cz.payola.model.components

import cz.payola.domain.rdf.DataCubeVocabulary
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent


import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model._
import org.apache.jena.riot._
import org.apache.jena.riot.lang._

/**
 * Model component for Data Cube Vocabulary
 * @author Jiri Helmich
 */
trait DataCubeModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val dataCubeModel = new
        {
            /**
             * Based on a URL, it downloads a definition and creates a Data Cube Vocabulary instance
             * @param url
             * @return
             */
            def loadVocabulary(url: String): cz.payola.common.rdf.DataCubeVocabulary = {
                DataCubeVocabulary(url)
            }

            private def datasetToJson(dataset: com.hp.hpl.jena.query.Dataset) : String = {
                com.github.jsonldjava.jena.JenaJSONLD.init()
                val outputStream = new java.io.ByteArrayOutputStream()
                RDFDataMgr.write(outputStream, dataset, com.github.jsonldjava.jena.JenaJSONLD.JSONLD);

                new String(outputStream.toByteArray(),"UTF-8")
            }

            private def cubeQuery(evaluationId: String, query: String) : com.hp.hpl.jena.query.Dataset = {
                rdfStorage.executeSPARQLQueryJena(query,"http://"+evaluationId)
            }

            def queryForCubeDSD(evaluationId: String) : String = {
                datasetToJson(cubeQuery(evaluationId, listDSDsQuery))
            }

            def listComponents(evaluationId: String, dsdUri: String, componentType: String = "dimension"): String = {
                datasetToJson(cubeQuery(evaluationId, listComponentsQuery(componentType, dsdUri)))
            }

            private def listComponentsQuery(componentType: String, dsdUri: String) : String =
                """
                  | PREFIX qb: <http://purl.org/linked-data/cube#>
                  | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                  |
                  | CONSTRUCT {
                  |     <{DSD_URI}> a qb:DataStructureDefinition ;
                  |                 qb:component ?id .
                  |     ?id qb:{COMPONENT_TYPE} ?x ;
                  |        rdfs:label ?l .
                  | } WHERE {
                  |     <{DSD_URI}> a qb:DataStructureDefinition ;
                  |                 qb:component ?c .
                  |     ?c qb:{COMPONENT_TYPE} ?x ;
                  |        rdfs:label ?l .
                  |     bind (iri(concat('http://example.org/id/', md5(concat(str(?x), str(?l))))) as ?id)
                  | }
                """.replace("{COMPONENT_TYPE}",componentType)
                   .replace("{DSD_URI}", dsdUri)
                   .stripMargin

            val listDSDsQuery =
                """
                  | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                  |
                  | CONSTRUCT {
                  |     ?d a qb:DataStructureDefinition ;
                  |        rdfs:label ?l .
                  |
                  | } WHERE {
                  |     ?d a qb:DataStructureDefinition .
                  |     OPTIONAL {
                  |         ?d rdfs:label ?l .
                  |     }
                  | }
                """.stripMargin
        }
}
