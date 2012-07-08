package cz.payola.web.shared

import cz.payola.model.DataFacade
import cz.payola.common.entities.Plugin
import scala.collection.mutable.ArrayBuffer

@remote object AnalysisBuilderData
{
    val df = new DataFacade

    def getPlugins() : Seq[Plugin] = {
        df.getPlugins()
    }

    def createInstance(id: String, params: Seq[String]) = {
        "done"
    }

    def saveBinding(sourceId: String, targetId: String) = {

    }
}
