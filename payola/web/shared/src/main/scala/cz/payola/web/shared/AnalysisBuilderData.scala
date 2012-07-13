package cz.payola.web.shared

import cz.payola.common.entities._
import s2js.compiler._
import cz.payola.domain.entities.User

@remote object AnalysisBuilderData
{
    @async
    @secured def createEmptyAnalysis(user: User = null)(successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.create(user).id)
    }

    @async def getPlugins()(successCallback: (Seq[Plugin] => Unit))(failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.pluginModel.getAll)
    }

    def lockAnalysis(id: String) {
    }

    def unlockAnalysis(id: String) {
    }

    @async def setAnalysisName(id: String, name: String)(successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getById(id)
        analysis.map { a =>
            a.name = name
            Payola.model.analysisModel.persist(a)
        }
        successCallback(true)
    }

    @async def createPluginInstance(pluginId: String, analysisId: String)(successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.pluginInstanceModel.create(pluginId, analysisId).id)
    }

    @async def setParameterValue(pluginInstanceId: String, parameterName: String, value: String)
        (successCallback: (Boolean => Unit))(failCallback: (Throwable => Unit)) {
        Payola.model.pluginInstanceModel.setParameterValue(pluginInstanceId, parameterName, value)
        successCallback(true)
    }

    @async def saveBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int)
        (successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        Payola.model.analysisModel.addBinding(analysisId, sourceId, targetId, inputIndex)
        successCallback(true)
    }

    @async def deletePluginInstance(pluginInstanceId: String)(successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        Payola.model.pluginInstanceModel.getById(pluginInstanceId).map(Payola.model.pluginInstanceModel.remove(_)).getOrElse{
            failCallback(new Exception("Unknown plugin instance."))
        }
        successCallback(true)
    }

    @async def getAnalysis(analysisId:String)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getById(analysisId)
        successCallback(analysis.get)
    }
}