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
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.analysis.AnalysisEditorView
import cz.payola.common.entities.plugins.DataSource
import scala.collection.mutable
import cz.payola.web.client.views.entity.DataSourceSelector

class AnalysisBuilder(parentElementId: String) extends Presenter
{
    protected val parentElement = document.getElementById(parentElementId)
    protected var allPlugins: Seq[Plugin] = List()
    protected var allSources: Seq[DataSource] = List()
    protected val saveAsYouTypeTimeout = 1000
    protected var analysisId = ""
    protected val timeoutMap = new HashMap[String, Int]
    protected var lanes = new ArrayBuffer[PluginInstance]
    protected var nameChangedTimeout: Option[Int] = None
    protected var descriptionChangedTimeout: Option[Int] = None
    protected val nameComponent = new TextInputControl("Analysis name", "init-name", "", "Enter analysis name")

    protected val view = new AnalysisEditorView

    def initialize() {
        val nameDialog = new Modal("Please, enter the name of the new analysis", List(nameComponent))
        nameDialog.render()

        nameDialog.saving += { e =>
            AnalysisBuilderData.setAnalysisName(analysisId, nameComponent.input.value) { success =>

                AnalysisBuilderData.createEmptyAnalysis(nameComponent.input.value) { id =>
                    analysisId = id
                    lockAnalysisAndLoadPlugins()
                    view.render(parentElement)
                    view.setName(nameComponent.input.value)
                } { error => fatalErrorHandler(error) }
            } { error => fatalErrorHandler(error) }
            true
        }

        nameDialog.closing += { e =>
            window.location.href = "/dashboard"
            true
        }
    }

    protected def lockAnalysisAndLoadPlugins() = {
        AnalysisBuilderData.lockAnalysis(analysisId)
        AnalysisBuilderData.getPlugins() { plugins => allPlugins = plugins}
        { error => fatalErrorHandler(error) }
        AnalysisBuilderData.getDataSources() { sources => allSources = sources}
        { error => fatalErrorHandler(error) }
    }

    view.description.input.changed += { eventArgs =>
        if (descriptionChangedTimeout.isDefined) {
            window.clearTimeout(descriptionChangedTimeout.get)
        }

        view.description.setIsActive()
        descriptionChangedTimeout = Some(window.setTimeout({ () =>
            AnalysisBuilderData.setAnalysisDescription(analysisId, view.description.input.value) { _ =>
                view.description.setIsActive(false)
                view.description.setOk()
            } { _ =>
                view.description.setIsActive(false)
                view.description.setError("Invalid description.")
            }
        }, saveAsYouTypeTimeout))
    }

    view.nameControl.input.changed += { eventArgs =>
        if (nameChangedTimeout.isDefined) {
            window.clearTimeout(nameChangedTimeout.get)
        }

        view.nameControl.setIsActive()
        nameChangedTimeout = Some(window.setTimeout({ () =>
            AnalysisBuilderData.setAnalysisName(analysisId, view.nameControl.input.value) { _ =>
                view.nameControl.setIsActive(false)
                view.nameControl.setOk()
            } { _ =>
                view.nameControl.setIsActive(false)
                view.nameControl.setError("Invalid name.")
            }
        }, saveAsYouTypeTimeout))

        false
    }

    view.addPluginLink.mouseClicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 0).filterNot(_.name == "Payola Private Storage"))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, None)
            dialog.destroy()
            false
        }
        dialog.render()
        false
    }

    def onDataSourceSelected(dataSource: DataSource){
        AnalysisBuilderData.cloneDataSource(dataSource.id, analysisId){ pi =>

            val map = new mutable.HashMap[String, String]

            pi.parameterValues.foreach{ paramValue =>
                map.put(paramValue.parameter.name, paramValue.value.toString)
            }

            val instance = new PluginInstance(pi.id,pi.plugin, List(), map)

            lanes.append(instance)
            view.renderInstance(instance)

            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            instance.parameterValueChanged += onParameterValueChanged
            instance.deleteButtonClicked += onDeleteClick
        }{ err => fatalErrorHandler(err) }
    }

    view.addDataSourceLink.mouseClicked += { event =>
        val dialog = new DataSourceSelector("Select one of available data sources:", allSources)
        dialog.dataSourceSelected += { e =>
            onDataSourceSelected(e.target)
            dialog.destroy()
        }

        dialog.render()
        false
    }

    view.mergeBranches.mouseClicked += { event =>
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
                        view.renderInstance(mergeInstance)

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
            view.renderInstance(instance)

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

    protected def onParameterValueChanged(args: EventArgs[ParameterValue]) {
        val parameterInfo = args.target
        val parameterId = parameterInfo.parameterId

        if (timeoutMap.contains(parameterId)) {
            window.clearTimeout(timeoutMap(parameterId))
        }

        parameterInfo.control.setIsActive()

        val timeoutId = window.setTimeout(() => {
            AnalysisBuilderData
                .setParameterValue(analysisId, parameterInfo.pluginInstanceId, parameterInfo.name, parameterInfo.value)
            { _ =>
                parameterInfo.control.setOk()
                parameterInfo.control.setIsActive(false)
            } { _ =>
                parameterInfo.control.setError("Wrong parameter value.")
                parameterInfo.control.setIsActive(false)
            }
        }, saveAsYouTypeTimeout)

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

        AnalysisBuilderData.deletePluginInstance(analysisId, instance.id) { _ =>
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

    def bind(a: PluginInstance, b: PluginInstance, inputIndex: Int) {
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
    def renderBinding(a: PluginInstance, b: PluginInstance) {}
}
