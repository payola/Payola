package cz.payola.web.shared

import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpoint

@remote object DatasourceBrowser
{
    def getInitialGraph(id: String) : Option[Graph] = {
        val instance = Payola.model.dataSourceModel.getById(id)

        if (instance.isDefined)
        {
            // WTF? How do you know that the instance is an instance of a sparql endpoint?
            val se = new SparqlEndpoint()
            Some(se.getFirstTriple(instance.get))
        }else{
            None
        }
    }
}
