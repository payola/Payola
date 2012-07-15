package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.views.todo.PluginInstance
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import s2js.adapters.js.browser.window
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.views.elements._
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.bootstrap._
import scala.Some

class AnalysisBuilder(menuHolder: String, pluginsHolder: String, nameHolder: String)
{
    protected var allPlugins: Seq[Plugin] = List()

    protected var analysisId = ""

    protected val timeoutMap = new HashMap[String, Int]
    protected val nameComponent = new InputControl("Analysis name", "init-name", "", "Enter analysis name")

    protected val name = new InputControl("Analysis name:", "name", "", "Analysis name")

    name.render(nameHolderElement)

    protected var lanes = new ArrayBuffer[PluginInstance]

    protected val menu = document.getElementById(menuHolder)

    protected val pluginsHolderElement = document.getElementById(pluginsHolder)

    protected val nameHolderElement = document.getElementById(nameHolder)

    protected val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    protected val addPluginLinkLi = new ListItem(List(addPluginLink))

    protected val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))

    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    protected val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    protected val mergeBranchesLi = new ListItem(List(mergeBranches))

    protected var nameChangedTimeout: Option[Int] = None

    AnalysisBuilderData.getPlugins() { plugins => allPlugins = plugins} { error => window.alert("Unable to load plugins")}
    init

    def init {
        val nameDialog = new Modal("Please, enter the name of the new analysis", List(nameComponent))

        AnalysisBuilderData.createEmptyAnalysis() {
            id =>
                analysisId = id
                AnalysisBuilderData.lockAnalysis(id)
                nameDialog.render()
        } { error => window.alert("Unable to create analysis")}

        nameDialog.saving += { e =>
            AnalysisBuilderData.setAnalysisName(analysisId, nameComponent.input.value) { success =>
                name.input.value = nameComponent.input.value
                nameDialog.destroy()
            } { error =>
                nameComponent.setError("Unable to use this name")
            }
            false
        }
    }

    name.input.changed += { eventArgs =>
        if (nameChangedTimeout.isDefined) {
            window.clearTimeout(nameChangedTimeout.get)
        }

        nameChangedTimeout = Some(window.setTimeout({ () =>
            AnalysisBuilderData.setAnalysisName(analysisId, name.input.value) { _ =>
                name.setOk()
            } { _ =>
                name.setError("Invalid name.")
            }
        }, 300))

        false
    }

    addPluginLinkLi.render(menu)
    addDataSourceLinkLi.render(menu)
    mergeBranchesLi.render(menu)

    addPluginLink.mouseClicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 0))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, None)
            dialog.destroy()
            false
        }
        dialog.render()
        false
    }

    mergeBranches.mouseClicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount > 1))
        dialog.pluginNameClicked += { evt =>

            dialog.destroy()

            val inputsCount = evt.target.inputCount
            if (inputsCount > lanes.size) {
                window.alert("The merge plugin has " + inputsCount.toString() + " inputs, but only " + lanes
                    .size + " branches are available.")
            } else {
                val mergeDialog = new MergeAnalysisBranchesDialog(lanes, inputsCount)
                mergeDialog.saving += { e =>
                    val instances = mergeDialog.outputToInstance

                    var i = 0
                    val buffer = new ArrayBuffer[PluginInstance]()

                    while (i < instances.size) {
                        buffer.append(instances(i))
                        instances(i).hideDeleteButton()
                        lanes -= instances(i)
                        i += 1
                    }

                    AnalysisBuilderData.createPluginInstance(evt.target.id, analysisId) { id =>
                        val mergeInstance = new PluginInstance(id, evt.target, buffer.asInstanceOf[Seq[PluginInstance]])
                        mergeInstance.render(pluginsHolderElement)

                        mergeInstance.connectButtonClicked += { clickedEvent =>
                            connectPlugin(mergeInstance)
                            false
                        }

                        mergeInstance.parameterValueChanged += onParameterValueChanged
                        mergeInstance.deleteButtonClicked += onDeleteClick

                        i = 0
                        buffer.map { instance: Any =>
                            bind(instance.asInstanceOf[PluginInstance], mergeInstance, i)
                            i += 1
                        }

                        lanes += mergeInstance

                        mergeDialog.destroy()
                    } { _ =>}
                    false
                }

                mergeDialog.render()
            }

            false
        }
        dialog.render()
        false
    }

    def onPluginNameClicked(plugin: Plugin, predecessor: Option[PluginInstance]) = {
        AnalysisBuilderData.createPluginInstance(plugin.id, analysisId) { id =>
            val instance = if (predecessor.isDefined) {
                new PluginInstance(id, plugin, List(predecessor.get))
            } else {
                new PluginInstance(id, plugin, List())
            }

            lanes.append(instance)

            instance.render(pluginsHolderElement)
            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            instance.parameterValueChanged += onParameterValueChanged
            instance.deleteButtonClicked += onDeleteClick

            predecessor.map { p =>
                lanes -= p
                p.hideDeleteButton()
                bind(p, instance, 0)
            }
        } { _ =>
        }
    }

    def onParameterValueChanged(args: EventArgs[ParameterValue]) {

        val parameterInfo = args.target
        val parameterId = parameterInfo.parameterId

        if (timeoutMap.contains(parameterId)) {
            window.clearTimeout(timeoutMap(parameterId))
        }

        val timeoutId = window.setTimeout(() => {
            AnalysisBuilderData
                .setParameterValue(parameterInfo.pluginInstanceId, parameterInfo.name, parameterInfo.value) { _ =>
                parameterInfo.control.setOk()
            } { _ =>
                parameterInfo.control.setError("Wrong parameter value.")
            }
        }, 300)

        timeoutMap.put(parameterId, timeoutId)
    }

    def connectPlugin(pluginInstance: PluginInstance): Unit = {
        val inner = pluginInstance

        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 1))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, Some(inner))
            dialog.destroy()
            false
        }
        dialog.render()
    }

    def onDeleteClick(eventArgs: EventArgs[PluginInstance]) {
        val instance = eventArgs.target

        AnalysisBuilderData.deletePluginInstance(instance.id) { _ =>
            lanes -= instance
            var i = 0
            while (i < instance.predecessors.size) {
                lanes += instance.predecessors(i)
                instance.predecessors(i).showDeleteButton()
                i += 1
            }
            instance.destroy()
        } { _ =>}
    }

    def bind(a: PluginInstance, b: PluginInstance, inputIndex: Int) = {
        AnalysisBuilderData.saveBinding(analysisId, a.id, b.id, inputIndex) { _ =>
            renderBinding(a, b)
        } { _ =>
            window.alert("Unable to save the binding")
        }
    }

    @javascript(
        """
          jsPlumb.repaintEverything();
          var settings = {
                            paintStyle:{ lineWidth:2, strokeStyle:"#BCE8F1", outlineColor:"#3A87AD", outlineWidth:1 },
                            connector:[ "Flowchart" ],
                            endpoint:[ "Dot", { radius:4 } ],
                            endpointStyle : { fillStyle: "#3A87AD"  },
                            anchor : [ "BottomCenter", "TopCenter" ]
                       };
          jsPlumb.connect({ source:a.getPluginElement(), target:b.getPluginElement() },settings);
        """)
    def renderBinding(a: PluginInstance, b: PluginInstance) = {}
}
