package cz.payola.web.shared

import cz.payola.common.entities._
import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.common.entities.plugins._

@secured
@remote object AnalysisBuilderData
{
    @async def createEmptyAnalysis(name: String, user: User = null)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.create(user, name))
    }

    @async def getPlugins(user: User = null)(successCallback: (Seq[Plugin] => Unit))(failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.pluginModel.getAccessibleToUser(Some(user)))
    }

    @async def cloneDataSourceAndBindToAnalysis(dataSourceId: String, analysisId: String, user: User = null)(successCallback: (PluginInstance => Unit))
        (failCallback: (Throwable => Unit)) {
        val dataSource = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user),dataSourceId).getOrElse {
            throw new Exception("DataSource not found.")
        }

        val analysis = Payola.model.analysisModel.getById(analysisId).getOrElse{
            throw new Exception("Analysis not found.")
        }

        val copy = dataSource.toInstance
        analysis.addPluginInstance(copy)

        successCallback(copy)
    }

    @async def lockAnalysis(id: String, user: User = null)(successCallback: (() => Unit))(failCallback: (Throwable => Unit)) {
        successCallback()
    }

    @async def unlockAnalysis(id: String, user: User = null)(successCallback: (() => Unit))(failCallback: (Throwable => Unit)) {
        successCallback()
    }

    @async def setAnalysisName(id: String, name: String, user: User = null)(successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getById(id)
        analysis.map {
            a =>
                a.name = name
                Payola.model.analysisModel.persist(a)
        }
        successCallback(true)
    }

    @async def setAnalysisDescription(id: String, description: String, user: User = null)
        (successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getById(id)
        analysis.map {
            a =>
                a.description = description
                Payola.model.analysisModel.persist(a)
        }
        successCallback(true)
    }

    @async def createPluginInstance(pluginId: String, analysisId: String, user: User = null)
        (successCallback: (PluginInstance => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.createPluginInstance(pluginId, analysisId))
    }

    @async def setParameterValue(analysisId: String, pluginInstanceId: String, parameterName: String, value: String,
        user: User = null)
        (successCallback: (() => Unit))(failCallback: (Throwable => Unit)) {
        Payola.model.analysisModel.setParameterValue(user, analysisId, pluginInstanceId, parameterName, value)
        successCallback()
    }

    @async def saveBinding(analysisId: String, sourceId: String, targetId: String, inputIndex: Int, user: User = null)
        (successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        Payola.model.analysisModel.addBinding(analysisId, sourceId, targetId, inputIndex)
        successCallback(true)
    }

    @async def deletePluginInstance(analysisId: String, pluginInstanceId: String, user: User = null)
        (successCallback: (Boolean => Unit))
        (failCallback: (Throwable => Unit)) {
        Payola.model.analysisModel.removePluginInstanceById(analysisId, pluginInstanceId)
        successCallback(true)
    }

    @async def getAnalysis(analysisId: String, user: User = null)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {
        val analysis = Payola.model.analysisModel.getById(analysisId)
        successCallback(analysis.get)
    }

    @async def getDataSources(user: User = null)(successCallback: (Seq[DataSource] => Unit))
        (failCallback: (Throwable => Unit)) {
        val sources = Payola.model.dataSourceModel.getAccessibleToUser(Some(user))
        successCallback(sources)
    }
}
