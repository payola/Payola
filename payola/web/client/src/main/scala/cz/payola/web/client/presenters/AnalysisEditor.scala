package cz.payola.web.client.presenters

import cz.payola.web.client.views.todo.PluginInstance
import cz.payola.web.shared.AnalysisBuilderData
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.common.entities
import s2js.runtime.client.scala.collection.immutable
import scala.collection.mutable

class AnalysisEditor(analysisIdP: String, menuHolder: String, pluginsHolder: String, nameHolder: String)
    extends AnalysisBuilder(menuHolder, pluginsHolder, nameHolder)
{
    private val creationMap = new HashMap[String, PluginInstance]

    analysisId = analysisIdP

    override def init {
        AnalysisBuilderData.getAnalysis(analysisIdP) { analysis =>

            analysisId = analysis.id

            lockAnalysisAndLoadPlugins

            setAnalysisNameToInputControl(analysis)

            val sources = new ArrayBuffer[PluginInstance]
            val renderBuffer = new ArrayBuffer[PluginInstance]

            loadInstancesData(analysis, sources, renderBuffer)
            processBindings(analysis)
            initialRender(sources, renderBuffer, analysis)

            true
        } { _ =>}
    }

    private def initialRender(sources: ArrayBuffer[PluginInstance], renderBuffer: ArrayBuffer[PluginInstance],
        analysis: entities.Analysis) {
        renderSources(sources, renderBuffer)
        renderBufferTopologically(renderBuffer)
        renderBindings(analysis)
    }

    private def renderBindings(analysis: entities.Analysis) {
        analysis.pluginInstanceBindings.map { b =>
            renderBinding(creationMap(b.sourcePluginInstance.id), creationMap(b.targetPluginInstance.id))
        }
    }

    private def renderBufferTopologically(renderBuffer: ArrayBuffer[PluginInstance]) {
        while (!renderBuffer.isEmpty) {
            renderBuffer.map { s =>
                val canRender: Boolean = checkInstanceCanRender(s, renderBuffer)
                if (canRender) {
                    s.render(pluginsHolderElement)
                    renderBuffer -= s
                }
            }
        }
    }

    private def checkInstanceCanRender(s: PluginInstance, renderBuffer: ArrayBuffer[PluginInstance]): Boolean = {
        var canRender = true
        s.predecessors.map { predecessor =>
            canRender = (canRender && !renderBuffer.contains(predecessor))
        }
        canRender
    }

    def renderSources(sources: ArrayBuffer[PluginInstance], renderBuffer: ArrayBuffer[PluginInstance]) {
        sources.map { s =>
            s.render(pluginsHolderElement)
            renderBuffer -= s
        }
    }

    private def processBindings(analysis: entities.Analysis) {
        analysis.pluginInstanceBindings.map { b =>
            val buff = new ArrayBuffer[PluginInstance]()
            creationMap(b.targetPluginInstance.id).predecessors.map(buff.append(_))
            buff.append(creationMap(b.sourcePluginInstance.id))

            creationMap(b.targetPluginInstance.id).predecessors = buff
        }
    }

    private def setAnalysisNameToInputControl(analysis: entities.Analysis) {
        name.input.value = analysis.name
    }

    protected def loadInstancesData(analysis: entities.Analysis, sources: ArrayBuffer[PluginInstance],
        renderBuffer: ArrayBuffer[PluginInstance]) {
        analysis.pluginInstances.map { instance =>

            val defaultValues = getDefaultValues(instance)
            val clientInstance = new PluginInstance(instance.id, instance.plugin, List(), defaultValues)
            clientInstance.hideDeleteButton()
            creationMap.put(instance.id, clientInstance)

            clientInstance.parameterValueChanged += onParameterValueChanged
            clientInstance.deleteButtonClicked += onDeleteClick
            clientInstance.connectButtonClicked += {e =>
                connectPlugin(clientInstance)
                false
            }

            if (!analysis.pluginInstanceBindings.find(_.sourcePluginInstance == instance).isDefined) {
                lanes += clientInstance
                clientInstance.showDeleteButton()
            }

            if (!analysis.pluginInstanceBindings.find(_.targetPluginInstance == instance).isDefined) {
                sources += clientInstance
            }

            renderBuffer += clientInstance
        }
    }

    private def getDefaultValues(instance: entities.plugins.PluginInstance): mutable.HashMap[String, String] = {
        val map = new mutable.HashMap[String, String]
        instance.parameterValues.map { v => map.put(v.parameter.name, v.value.toString)}
        map
    }
}
