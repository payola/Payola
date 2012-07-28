package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.views.todo.PluginInstanceView
import cz.payola.web.client.views.todo.EditablePluginInstanceView
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import s2js.adapters.js.browser.window
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.analysis.AnalysisEditorView
import cz.payola.common.entities.plugins.DataSource
import scala.collection.mutable
import cz.payola.web.client.views.entity.DataSourceSelector
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class AnalysisBuilder(parentElementId: String) extends Presenter
{
    protected val parentElement = document.getElementById(parentElementId)

    protected var allPlugins: Seq[Plugin] = List()

    protected var allSources: Seq[DataSource] = List()

    protected val saveAsYouTypeTimeout = 1000

    protected var analysisId = ""

    protected val timeoutMap = new HashMap[String, Int]

    protected var branches = new ArrayBuffer[PluginInstanceView]

    protected var nameChangedTimeout: Option[Int] = None

    protected var descriptionChangedTimeout: Option[Int] = None

    protected val nameComponent = new TextInputControl("Analysis name", "init-name", "", "Enter analysis name")

    protected val instancesMap = new mutable.HashMap[String, PluginInstanceView]

    def initialize() {
        val nameDialog = new Modal("Please, enter the name of the new analysis", List(nameComponent))
        nameDialog.render()

        nameDialog.confirming += {
            e =>
                AnalysisBuilderData.setAnalysisName(analysisId, nameComponent.input.value) {
                    success =>

                        AnalysisBuilderData.createEmptyAnalysis(nameComponent.input.value) {
                            analysis =>
                                analysisId = analysis.id
                                lockAnalysisAndLoadPlugins()
                                val view = new AnalysisEditorView(analysis)
                                view.visualiser.pluginInstanceRendered += {
                                    e => instancesMap.put(e.target.id, e.target)
                                }
                                view.render(parentElement)
                                view.setName(nameComponent.input.value)

                                bindMenuEvents(view)
                        } {
                            error => fatalErrorHandler(error)
                        }
                } {
                    error => fatalErrorHandler(error)
                }
                true
        }

        nameDialog.closing += {
            e =>
                window.location.href = "/dashboard"
                true
        }
    }

    protected def bindMenuEvents(view: AnalysisEditorView) {
        view.description.input.changed += {
            eventArgs =>
                if (descriptionChangedTimeout.isDefined) {
                    window.clearTimeout(descriptionChangedTimeout.get)
                }

                view.description.setIsActive()
                descriptionChangedTimeout = Some(window.setTimeout({
                    () =>
                        AnalysisBuilderData.setAnalysisDescription(analysisId, view.description.input.value) {
                            _ =>
                                view.description.setIsActive(false)
                                view.description.setOk()
                        } {
                            _ =>
                                view.description.setIsActive(false)
                                view.description.setError("Invalid description.")
                        }
                }, saveAsYouTypeTimeout))
        }

        view.nameControl.input.changed += {
            eventArgs =>
                if (nameChangedTimeout.isDefined) {
                    window.clearTimeout(nameChangedTimeout.get)
                }

                view.nameControl.setIsActive()
                nameChangedTimeout = Some(window.setTimeout({
                    () =>
                        AnalysisBuilderData.setAnalysisName(analysisId, view.nameControl.input.value) {
                            _ =>
                                view.nameControl.setIsActive(false)
                                view.nameControl.setOk()
                        } {
                            _ =>
                                view.nameControl.setIsActive(false)
                                view.nameControl.setError("Invalid name.")
                        }
                }, saveAsYouTypeTimeout))

                false
        }

        view.addPluginLink.mouseClicked += {
            event =>
                val dialog = new
                        PluginDialog(allPlugins.filter(_.inputCount == 0).filterNot(_.name == "Payola Private Storage"))
                dialog.pluginNameClicked += {
                    evtArgs =>
                        onPluginNameClicked(evtArgs.target, None, view)
                        dialog.destroy()
                        false
                }
                dialog.render()
                false
        }

        view.addDataSourceLink.mouseClicked += {
            event =>
                val dialog = new DataSourceSelector("Select one of available data sources:", allSources)
                dialog.dataSourceSelected += {
                    e =>
                        onDataSourceSelected(e.target, view)
                        dialog.destroy()
                }

                dialog.render()
                false
        }

        view.mergeBranches.mouseClicked += {
            event =>
                val dialog = new PluginDialog(allPlugins.filter(_.inputCount > 1))
                dialog.pluginNameClicked += {
                    evt =>

                        dialog.destroy()

                        val inputsCount = evt.target.inputCount
                        if (inputsCount > branches.size) {
                            AlertModal.display(
                                "The plugin can't be used", "The merge plugin has " + inputsCount.toString +
                                " inputs, but only " + branches.size + " branches are available."
                            )
                        } else {
                            val mergeDialog = new MergeAnalysisBranchesDialog(branches, inputsCount)
                            mergeDialog.confirming += { e =>
                                    val instances = mergeDialog.outputToInstance
                                    val buffer = new ArrayBuffer[PluginInstanceView]()

                                    var i = 0
                                    while(i < instances.size) {
                                        buffer.append(instances(i))
                                        instances(i).hideControls()
                                        branches -= instances(i)
                                        i = i+1
                                    }

                                    AnalysisBuilderData.createPluginInstance(evt.target.id, analysisId) {
                                        id =>
                                            val mergeInstance = new EditablePluginInstanceView(id, evt.target,
                                                buffer.asInstanceOf[Seq[PluginInstanceView]])
                                            view.visualiser.renderPluginInstanceView(mergeInstance)

                                            mergeInstance.connectButtonClicked += {
                                                clickedEvent =>
                                                    connectPlugin(mergeInstance, view)
                                                    false
                                            }

                                            mergeInstance.parameterValueChanged += onParameterValueChanged
                                            mergeInstance.deleteButtonClicked += onDeleteClick

                                            i = 0
                                            buffer.map {
                                                instance: Any =>
                                                    bind(instance.asInstanceOf[PluginInstanceView], mergeInstance, i)
                                                    i += 1
                                            }

                                            branches += mergeInstance
                                            mergeDialog.destroy()
                                    } {
                                        _ =>
                                    }
                                    false
                            }

                            mergeDialog.render()
                        }

                        false
                }
                dialog.render()
                false
        }
    }

    protected def lockAnalysisAndLoadPlugins() = {
        AnalysisBuilderData.lockAnalysis(analysisId)
        AnalysisBuilderData.getPlugins() {
            plugins => allPlugins = plugins
        } {
            error => fatalErrorHandler(error)
        }
        AnalysisBuilderData.getDataSources() {
            sources => allSources = sources
        } {
            error => fatalErrorHandler(error)
        }
    }

    def onDataSourceSelected(dataSource: DataSource, view: AnalysisEditorView) {
        AnalysisBuilderData.cloneDataSource(dataSource.id, analysisId) {
            pi =>

                val map = new mutable.HashMap[String, String]

                pi.parameterValues.foreach {
                    paramValue =>
                        map.put(paramValue.parameter.name, paramValue.value.toString)
                }

                val instance = new EditablePluginInstanceView(pi.id, pi.plugin, List(), map)

                branches.append(instance)
                view.visualiser.renderPluginInstanceView(instance)

                instance.connectButtonClicked += onConnectClicked(view)

                instance.parameterValueChanged += onParameterValueChanged
                instance.deleteButtonClicked += onDeleteClick
        } {
            err => fatalErrorHandler(err)
        }
    }

    protected def onConnectClicked(view: AnalysisEditorView): (EventArgs[PluginInstanceView]) => Unit = {
        evt =>
            connectPlugin(evt.target, view)
            false
    }

    def onPluginNameClicked(plugin: Plugin, predecessor: Option[PluginInstanceView], view: AnalysisEditorView) = {
        AnalysisBuilderData.createPluginInstance(plugin.id, analysisId) {
            id =>
                val instance = if (predecessor.isDefined) {
                    new EditablePluginInstanceView(id, plugin, List(predecessor.get))
                } else {
                    new EditablePluginInstanceView(id, plugin, List())
                }

                branches.append(instance)
                view.visualiser.renderPluginInstanceView(instance)

                instance.connectButtonClicked += {
                    evt =>
                        connectPlugin(evt.target, view)
                        false
                }

                instance.parameterValueChanged += onParameterValueChanged
                instance.deleteButtonClicked += onDeleteClick

                predecessor.map {
                    p =>
                        branches -= p
                        p.hideControls()
                        bind(p, instance, 0)
                }
        } {
            _ =>
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
                .setParameterValue(analysisId, parameterInfo.pluginInstanceId, parameterInfo.name, parameterInfo.value) {
                _ =>
                    parameterInfo.control.setOk()
                    parameterInfo.control.setIsActive(false)
            } {
                _ =>
                    parameterInfo.control.setError("Wrong parameter value.")
                    parameterInfo.control.setIsActive(false)
            }
        }, saveAsYouTypeTimeout)

        timeoutMap.put(parameterId, timeoutId)
    }

    def connectPlugin(pluginInstance: PluginInstanceView, view: AnalysisEditorView): Unit = {
        val inner = pluginInstance

        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 1))
        dialog.pluginNameClicked += {
            evtArgs =>
                onPluginNameClicked(evtArgs.target, Some(inner), view)
                dialog.destroy()
                false
        }
        dialog.render()
    }

    def onDeleteClick(eventArgs: EventArgs[PluginInstanceView]) {
        val instance = eventArgs.target

        AnalysisBuilderData.deletePluginInstance(analysisId, instance.id) {
            _ =>
                branches -= instance
                var i = 0
                while (i < instance.predecessors.size) {
                    branches += instance.predecessors(i)
                    instance.predecessors(i).showControls()
                    i += 1
                }
                instance.destroy()
        } {
            _ =>
        }
    }

    def bind(a: PluginInstanceView, b: PluginInstanceView, inputIndex: Int) {
        AnalysisBuilderData.saveBinding(analysisId, a.id, b.id, inputIndex) {
            _ =>
                renderBinding(a, b)
        }(fatalErrorHandler(_))
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
    def renderBinding(a: PluginInstanceView, b: PluginInstanceView) {}

    protected def setTimeout(key: String, callback: () => Unit) {
        timeoutMap.put(key, window.setTimeout(callback, saveAsYouTypeTimeout))
    }

    protected def clearTimeOutIfSet(key: String) {
        if (timeoutMap.get(key).isDefined) {
            window.clearTimeout(timeoutMap.get(key).get)
        }
    }
}
