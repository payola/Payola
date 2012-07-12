package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import s2js.adapters.js.browser.window
import cz.payola.web.client.events._
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap
import scala.Some
import cz.payola.web.client.presenters.models.ParameterValue

class AnalysisEditor(analysisIdP: String, menuHolder: String, pluginsHolder: String, nameHolder: String)
    extends AnalysisBuilder(menuHolder,pluginsHolder,nameHolder)
{
    private val creationMap = new HashMap[String,PluginInstance]

    analysisId = analysisIdP

    override def init {
        AnalysisBuilderData.getAnalysis(analysisIdP){ analysis =>
            name.setValue(analysis.name)

            analysis.pluginInstances.map{ instance =>

                val clientInstance = new PluginInstance(instance.id,instance.plugin)
                clientInstance.hideDeleteButton()
                creationMap.put(instance.id, clientInstance)

                if (!analysis.pluginInstanceBindings.find(_.sourcePluginInstance == instance).isDefined){
                    lanes += clientInstance
                    clientInstance.showDeleteButton()
                }
            }

            analysis.pluginInstanceBindings.map{b=>
                val buff = new ArrayBuffer[PluginInstance]()
                creationMap(b.targetPluginInstance.id).predecessors.map(buff.append(_))
                buff.append(creationMap(b.sourcePluginInstance.id))

                creationMap(b.targetPluginInstance.id).predecessors = buff
            }

            lanes.map(_.render(pluginsHolderElement))

            analysis.pluginInstanceBindings.map{ b =>
                renderBinding(creationMap(b.sourcePluginInstance.id), creationMap(b.targetPluginInstance.id))
            }

            true

        }
        { _ =>  }
    }
}
