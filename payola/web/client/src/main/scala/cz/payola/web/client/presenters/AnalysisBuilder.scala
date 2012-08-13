package cz.payola.web.client.presenters

import s2js.adapters.browser._
import cz.payola.web.client.views.todo.PluginInstanceView
import cz.payola.web.client.views.todo.EditablePluginInstanceView
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import cz.payola.common.entities.Plugin
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.analysis.AnalysisEditorView
import cz.payola.common.entities.plugins.DataSource
import scala.collection.mutable
import cz.payola.web.client.views.entity.plugins.DataSourceSelector
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.common.ValidationException
import cz.payola.web.client.views.elements.form.fields.TextInput

class AnalysisBuilder(parentElementId: String) extends Presenter
{
    protected val parentElement = document.getElementById(parentElementId)

    protected var allPlugins: Seq[Plugin] = List()

    protected var allSources: Seq[DataSource] = List()

    protected var analysisId = ""

    protected var branches = new ArrayBuffer[PluginInstanceView]

    protected var nameComponent = new InputControl(
        "Analysis name",
        new TextInput("init-name", "", "Enter analysis name")
    )

    protected val instancesMap = new mutable.HashMap[String, PluginInstanceView]

    def initialize() {
        val nameDialog = new Modal("Please, enter the name of the new analysis", List(nameComponent))
        nameDialog.render()

        nameDialog.confirming += {
            e =>
                AnalysisBuilderData.setAnalysisName(analysisId, nameComponent.field.value) {
                    success =>

                        AnalysisBuilderData.createEmptyAnalysis(nameComponent.field.value) {
                            analysis =>
                                analysisId = analysis.id
                                lockAnalysisAndLoadPlugins()
                                val view = new AnalysisEditorView(analysis, Some(nameComponent.field.value), None)
                                view.visualizer.pluginInstanceRendered += {
                                    e => instancesMap.put(e.target.pluginInstance.id, e.target)
                                }
                                view.render(parentElement)
                                view.setName(nameComponent.field.value)

                                bindMenuEvents(view)

                                nameDialog.destroy()
                        } {
                            error =>
                                error match {
                                    case rpc: ValidationException => {
                                        AlertModal.display("Validation failed", rpc.message)
                                        false
                                    }
                                    case _ => fatalErrorHandler(error)
                                }
                        }
                } {
                    error => fatalErrorHandler(error)
                }
                false
        }

        nameDialog.closing += {
            e =>
                window.location.href = "/dashboard"
                true
        }
    }

    protected def bindMenuEvents(view: AnalysisEditorView) {
        view.description.delayedChanged += { _ =>
            view.description.isActive = true
            AnalysisBuilderData.setAnalysisDescription(analysisId, view.description.field.value) { _ =>
                view.description.isActive = false
                view.description.setOk()
            } { _ =>
                view.description.isActive = false
                view.description.setError("Invalid description.")
            }
        }

        view.name.delayedChanged += { _ =>
            view.name.isActive = true
            AnalysisBuilderData.setAnalysisName(analysisId, view.name.field.value) { _ =>
                view.name.isActive = false
                view.name.setOk()
            } { _ =>
                view.name.isActive = false
                view.name.setError("Invalid name.")
            }
        }

        view.addPluginLink.mouseClicked += { _ =>
            val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 0).filterNot(_.name == "Payola Private Storage"))
            dialog.pluginNameClicked += { evtArgs =>
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

                                    if(mergeDialog.outputToInstance.size < inputsCount){
                                        mergeDialog.destroy()
                                        AlertModal.display("Not enough inputs bound","You need to bind all the inputs provided.")
                                    } else {
                                        mergeBranches(instances, buffer, evt, view, mergeDialog)
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

    def mergeBranches(instances: mutable.HashMap[Int, PluginInstanceView], buffer: ArrayBuffer[PluginInstanceView],
        evt: EventArgs[Plugin], view: AnalysisEditorView, mergeDialog: MergeAnalysisBranchesDialog) {
        var i = 0
        while (i < instances.size) {
            buffer.append(instances(i))
            instances(i).hideControls()
            branches -= instances(i)
            i = i + 1
        }

        AnalysisBuilderData.createPluginInstance(evt.target.id, analysisId) { createdInstance =>
                val mergeInstance = new EditablePluginInstanceView(createdInstance, buffer.asInstanceOf[Seq[PluginInstanceView]])
                view.visualizer.renderPluginInstanceView(mergeInstance)

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
    }

    protected def lockAnalysisAndLoadPlugins() = {
        AnalysisBuilderData.lockAnalysis(analysisId) { () =>
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
        } {
            error => fatalErrorHandler(error)
        }
    }

    def onDataSourceSelected(dataSource: DataSource, view: AnalysisEditorView) {
        blockPage("Making the data source available...")
        AnalysisBuilderData.cloneDataSource(dataSource.id, analysisId) { pi =>
            val map = new mutable.HashMap[String, String]

            pi.parameterValues.foreach {
                paramValue =>
                    map.put(paramValue.parameter.name, paramValue.value.toString)
            }

            val instance = new EditablePluginInstanceView(pi, List())

            branches.append(instance)
            view.visualizer.renderPluginInstanceView(instance)

            instance.connectButtonClicked += onConnectClicked(view)

            instance.parameterValueChanged += onParameterValueChanged
            instance.deleteButtonClicked += onDeleteClick

            unblockPage()
        } {
            err => fatalErrorHandler(err)
                unblockPage()
        }
    }

    protected def onConnectClicked(view: AnalysisEditorView): (EventArgs[PluginInstanceView]) => Unit = { evt =>
        connectPlugin(evt.target, view)
        false
    }

    def onPluginNameClicked(plugin: Plugin, predecessor: Option[PluginInstanceView], view: AnalysisEditorView) = {
        blockPage("Creating an instance of the plugin...")

        AnalysisBuilderData.createPluginInstance(plugin.id, analysisId) { createdInstance =>
            val instance = if (predecessor.isDefined) {
                new EditablePluginInstanceView(createdInstance, List(predecessor.get))
            } else {
                new EditablePluginInstanceView(createdInstance, List())
            }

            branches.append(instance)
            view.visualizer.renderPluginInstanceView(instance)

            instance.connectButtonClicked += { evt =>
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

            unblockPage()
        } {
            _ => unblockPage()
        }
    }

    protected def onParameterValueChanged(args: EventArgs[ParameterValue]) {
        val parameterInfo = args.target
        parameterInfo.control.isActive = true
        AnalysisBuilderData.setParameterValue(analysisId, parameterInfo.pluginInstanceId, parameterInfo.name, parameterInfo.value) { () =>
            parameterInfo.control.setOk()
            parameterInfo.control.isActive = false
        } { _ =>
            parameterInfo.control.setError("Wrong parameter value.")
            parameterInfo.control.isActive = false
        }
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
        blockPage("Deleting...")
        AnalysisBuilderData.deletePluginInstance(analysisId, instance.pluginInstance.id) {
            _ =>
                branches -= instance
                var i = 0
                while (i < instance.predecessors.size) {
                    branches += instance.predecessors(i)
                    instance.predecessors(i).showControls()
                    i += 1
                }
                instance.destroy()
                unblockPage()
        } {
            _ => unblockPage()
                AlertModal.display("Error when deleting","The plugin could not be deleted.")
        }
    }

    def bind(a: PluginInstanceView, b: PluginInstanceView, inputIndex: Int) {
        AnalysisBuilderData.saveBinding(analysisId, a.pluginInstance.id, b.pluginInstance.id, inputIndex) {
            _ =>
        }(fatalErrorHandler(_))
    }
}
