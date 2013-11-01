package cz.payola.model.components

import cz.payola.domain.rdf.DataCubeVocabulary
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent

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

            def queryForCubeDSD(evaluationId: String, entityType: String) = {
                rdfStorage.executeSPARQLQuery("CONSTRUCT { ?s ?p ?o . } WHERE { ?s a <http://purl.org/linked-data/cube#DataStructureDefinition> ; ?p ?o . }","http://"+evaluationId)
            }
        }
}
