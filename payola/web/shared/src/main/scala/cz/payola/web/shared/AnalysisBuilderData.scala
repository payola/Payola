package cz.payola.web.shared

import cz.payola.common.entities.Plugin
import scala.collection.mutable.ArrayBuffer

@remote object AnalysisBuilderData
{
     def getPlugins() : Seq[Plugin] = {
        Payola.model.pluginModel.getAll
    }

    def createInstance(id: String, params: Seq[String]) = {
        "done"
    }

    def saveBinding(sourceId: String, targetId: String) = {

    }
}
