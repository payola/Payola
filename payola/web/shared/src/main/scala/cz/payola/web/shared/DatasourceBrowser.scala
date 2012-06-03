package cz.payola.web.shared

import cz.payola.domain.entities.analyses.evaluation._
import cz.payola.data.entities.dao.FakeAnalysisDAO
import scala.collection.mutable.HashMap
import cz.payola.common.rdf.Graph
import cz.payola.model.DataFacade
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint

@remote object DatasourceBrowser
{
    def getInitialGraph(id: String) : Option[Graph] = {
        val df = new DataFacade
        val instance = df.getDataSourceById(id)

        if (instance.isDefined)
        {
            val se = new SparqlEndpoint()
            Some(se.getFirstTriple(instance.get))
        }else{
            None
        }
    }
}
