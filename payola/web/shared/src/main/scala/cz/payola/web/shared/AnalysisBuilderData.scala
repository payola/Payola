package cz.payola.web.shared

import cz.payola.common.entities.Plugin
import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.parameters._

@remote object AnalysisBuilderData
{
    @async
    @secured def createEmptyAnalysis(user: User = null)(successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.create(user).id)
    }

    @async def getPlugins()(successCallback: (Seq[Plugin] => Unit))(failCallback: (Throwable => Unit)) {
        try { {
            successCallback(Payola.model.pluginModel.getAll)
        }
        } catch {
            case e: Exception => failCallback(e)
        }
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

    def saveBinding(sourceId: String, targetId: String) {
    }
}
