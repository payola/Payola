package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.web.shared._
import cz.payola.common.rdf.DataCubeVocabulary

/**
 * Remote proxy for RDF, especially DataCube
 * @author Jiri Helmich
 */
@remote object RDFManager
{
    @async def parseDataCubeVocabulary(url: String)
        (successCallback: DataCubeVocabulary => Unit)
        (errorCallback: Throwable => Unit) {

        val vocabulary = Payola.model.dataCubeModel.loadVocabulary(url)

        successCallback(vocabulary)

    }
}