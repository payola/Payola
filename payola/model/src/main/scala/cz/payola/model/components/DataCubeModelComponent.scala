package cz.payola.model.components

import cz.payola.domain.rdf.DataCubeVocabulary

/**
 *
 */
trait DataCubeModelComponent
{
    lazy val dataCubeModel = new
        {
            def loadVocabulary(url: String): cz.payola.common.rdf.DataCubeVocabulary = {
                DataCubeVocabulary(url)
            }
        }
}
