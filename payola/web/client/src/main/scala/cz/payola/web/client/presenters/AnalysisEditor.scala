package cz.payola.web.client.presenters

import cz.payola.web.client.views.todo.PluginInstance
import cz.payola.web.shared.AnalysisBuilderData
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap

class AnalysisEditor(analysisIdP: String, menuHolder: String, pluginsHolder: String, nameHolder: String)
    extends AnalysisBuilder(menuHolder,pluginsHolder,nameHolder)
{
    private val creationMap = new HashMap[String,PluginInstance]

    analysisId = analysisIdP

    override def init {
        AnalysisBuilderData.getAnalysis(analysisIdP){ analysis =>
            name.input.value = analysis.name

            val sources = new ArrayBuffer[PluginInstance]
            val renderBuffer = new ArrayBuffer[PluginInstance]

            analysis.pluginInstances.map{ instance =>

                val clientInstance = new PluginInstance(instance.id,instance.plugin)
                clientInstance.hideDeleteButton()
                creationMap.put(instance.id, clientInstance)

                if (!analysis.pluginInstanceBindings.find(_.sourcePluginInstance == instance).isDefined){
                    lanes += clientInstance
                    clientInstance.showDeleteButton()
                }

                if (!analysis.pluginInstanceBindings.find(_.targetPluginInstance == instance).isDefined){
                    sources += clientInstance
                }

                renderBuffer += clientInstance
            }

            analysis.pluginInstanceBindings.map{b=>
                val buff = new ArrayBuffer[PluginInstance]()
                creationMap(b.targetPluginInstance.id).predecessors.map(buff.append(_))
                buff.append(creationMap(b.sourcePluginInstance.id))

                creationMap(b.targetPluginInstance.id).predecessors = buff
            }

            sources.map{s =>
                s.render(pluginsHolderElement)
                renderBuffer -= s
            }

            while (!renderBuffer.isEmpty)
            {
                renderBuffer.map{s =>
                    var canRender = true
                    s.predecessors.map{predecessor =>
                        canRender = (canRender && !renderBuffer.contains(predecessor))
                    }

                    if (canRender){
                        s.render(pluginsHolderElement)
                        renderBuffer -= s
                    }
                }
            }

            analysis.pluginInstanceBindings.map{ b =>
                renderBinding(creationMap(b.sourcePluginInstance.id), creationMap(b.targetPluginInstance.id))
            }

            true

        }
        { _ =>  }
    }
}
