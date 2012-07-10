package cz.payola.web.shared

import cz.payola.common.entities.Plugin
import s2js.compiler.async

@remote object AnalysisBuilderData
{
    @async def createEmptyAnalysis()(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.create.id)
    }

    @async def getPlugins()(successCallback: (Seq[Plugin] => Unit))(failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.pluginModel.getAll)
    }

    def lockAnalysis(id: String) {

    }

    def unlockAnalysis(id: String) {

    }

    @async def setAnalysisName(id: String, name: String)(successCallback: (Boolean => Unit))(failCallback: (Throwable => Unit)) {
        Payola.model.analysisModel.getById(id).map(_.name = name)
        successCallback(true)
    }

    def createInstance(id: String, params: Seq[String]) = {
        "done"
    }

    def saveBinding(sourceId: String, targetId: String) {

    }
}
