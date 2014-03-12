package cz.payola.model.components

import cz.payola.domain.rdf.DataCubeVocabulary
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.sparql.VariableGenerator

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

            private def datasetToJson(dataset: com.hp.hpl.jena.query.Dataset): String = {
                val outputStream = new java.io.ByteArrayOutputStream()
                dataset.getDefaultModel().write(outputStream, "RDF/JSON")

                new String(outputStream.toByteArray(), "UTF-8")
            }

            private def cubeQuery(evaluationId: String, query: String): com.hp.hpl.jena.query.Dataset = {
                rdfStorage.executeSPARQLQueryJena(query, "http://" + evaluationId)
            }

            def queryForCubeDSD(evaluationId: String): String = {
                datasetToJson(cubeQuery(evaluationId, listDSDsQuery))
            }

            def queryForCubeDefinitions(evaluationId: String): String = {
                datasetToJson(cubeQuery(evaluationId, getCubeDefinitions))
            }

            def listComponents(evaluationId: String, dsdUri: String, componentType: String = "dimension"): String = {
                datasetToJson(cubeQuery(evaluationId, listComponentsQuery(componentType, dsdUri)))
            }

            def distinctValues(evaluationId: String, property: String, isDate: Boolean): String = {
                datasetToJson(cubeQuery(evaluationId, if (isDate) {
                    distinctDateValuesQuery(property)
                } else {
                    buildDistinctValuesQuery(property)
                }))
            }

            def dataset(evaluationId: String, measure: String, dimension: String, filters: List[String]): String = {
                datasetToJson(cubeQuery(evaluationId, buildDatasetQuery(measure, dimension, filters)))
            }

            private def buildDatasetQuery(measure: String, dimension: String, filters: List[String]): String = {

                val gen = new VariableGenerator

                var additionalWhere = ""
                val positiveFilters = filters.filter(_.startsWith("+")).map {
                    f =>
                        val parts = f.substring(1).split("\\$:\\$:\\$")
                        if(parts(2) == "true"){
                            val dateVar = gen.apply()
                            additionalWhere += String.format(" BIND(SUBSTR(str(%s),1,4) AS ?y) FILTER(?y = %s) ", dateVar, parts(1))
                            String.format("""   <%s>    %s """.stripMargin, parts(0), dateVar)
                        }else{
                            String.format( """   <%s>    %s""", parts(0), parts(1))
                        }
                }.mkString( """ ;\n""") + " ."

                val negativeFilters = filters.filter(_.startsWith("-")).map {
                    f =>
                        val parts = f.substring(1).split("\\$:\\$:\\$")
                        val v = gen.apply()
                        String.format( """ OPTIONAL { %s <%s> %s . FILTER (?x = %s) } FILTER ( !BOUND(%s) ) """, v, parts(0), parts(1), v, v)
                }.mkString(""" """)

                val q = String.format(
                    """
                      | SELECT DISTINCT ?d ?m
                      | WHERE {
                      |     ?x  <%s> ?m ;
                      |         <%s> ?d ;
                      |     %s
                      |     %s
                      |     %s
                      | }
                    """, measure, dimension, positiveFilters, additionalWhere, negativeFilters).stripMargin

                q
            }

            private def distinctDateValuesQuery(property: String): String = {
                String.format(
                    """
                      |SELECT DISTINCT ?o
                      |WHERE {
                      |?x <%s> ?date .
                      |BIND(substr(str(?date),1,4) AS ?o)
                      |} ORDER BY ?o
                    """.stripMargin, property)
            }

            private def buildDistinctValuesQuery(property: String): String = {
                String.format(
                    """
                      | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                      |
                      | SELECT DISTINCT ?o ?l WHERE {
                      |     [] <%s> ?o .
                      |
                      |     OPTIONAL { ?o rdfs:label ?l . }
                      |     FILTER (LANG(?l) = 'en')
                      |
                      | } ORDER BY(?o)
                      | """.stripMargin, property)
            }

            private def listComponentsQuery(componentType: String, dsdUri: String): String =
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
                """.replace("{COMPONENT_TYPE}", componentType)
                    .replace("{DSD_URI}", dsdUri)
                    .stripMargin

            val listDSDsQuery =
                """
                  | PREFIX qb: <http://purl.org/linked-data/cube#>
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

            val getCubeDefinitions =
                """
                  | PREFIX qb: <http://purl.org/linked-data/cube#>
                  | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                  |
                  | CONSTRUCT {
                  |     ?d a qb:DataStructureDefinition ;
                  |        qb:component ?c ;
                  |        qb:component ?c2 .
                  |     ?c qb:dimension ?dim ;
                  |        rdfs:label ?l .
                  |     ?c2 qb:measure ?m ;
                  |         rdfs:label ?l2 .
                  | } WHERE {
                  |     ?d a qb:DataStructureDefinition .
                  |         ?d qb:component ?c .
                  |         ?c qb:dimension ?dim ;
                  |             rdfs:label ?l .
                  |         ?d qb:component ?c2 .
                  |         ?c2 qb:measure ?m ;
                  |             rdfs:label ?l2 .
                  |
                  |     bind (iri(concat('http://example.org/id/', md5(concat(str(?x), str(?l))))) as ?id)
                  | }
                """.stripMargin
        }
}
