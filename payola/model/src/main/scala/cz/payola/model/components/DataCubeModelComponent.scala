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

            def queryForCubeDSD(evaluationId: String, format: String = "JSON-LD") : String = {
                com.github.jsonldjava.jena.JenaJSONLD.init()

                val dataset = rdfStorage.executeSPARQLQueryJena("CONSTRUCT { ?s ?p ?o . } WHERE { ?s ?p ?o . } LIMIT 100","http://"+evaluationId)
                val outputStream = new java.io.ByteArrayOutputStream()
                RDFDataMgr.write(outputStream, dataset, com.github.jsonldjava.jena.JenaJSONLD.JSONLD);

                new String(outputStream.toByteArray(),"UTF-8")
            }
        }
}
