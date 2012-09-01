package cz.payola.web.client.presenters.entity.analyses

import s2js.adapters.browser._
import cz.payola.web.client.views.entity.plugins.{EditablePluginInstanceView, PluginInstanceView, DataSourceSelector}
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import cz.payola.common.entities.Plugin
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.entity.analysis.AnalysisEditorView
import cz.payola.common.entities.plugins._
import scala.collection.mutable
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.common.ValidationException
import cz.payola.web.client.views.elements.form.fields.TextInput
import cz.payola.web.client.views.elements._
import scala.Some
import s2js.runtime.shared.rpc.RpcException
import scala.Some

/**
 * Presenter responsible for the logic of the Analysis Builder editor.
 * @param parentElementId ID of the DOM element to render views into
 */
class AnalysisBuilder(parentElementId: String) extends Presenter
{
    protected val parentElement = document.getElementById(parentElementId)
    protected var allPlugins: Seq[Plugin] = List()
    protected var allSources: Seq[DataSource] = List()
    protected val instancesMap = new mutable.HashMap[String, PluginInstanceView]
    protected var analysisId = ""
    protected var branches = new ArrayBuffer[PluginInstanceView]
    protected var nameComponent = new InputControl(
        "Analysis name",
        new TextInput("init-name", "", "Enter analysis name"), Some("span2")
    )

    def initialize() {
        val nameDialog = new Modal("Please, enter the name of the new analysis", List(nameComponent))
        nameDialog.render()

        nameDialog.confirming += {
            e =>
                nameDialog.block("Initializing analysis...")
                AnalysisBuilderData.setAnalysisName(analysisId, nameComponent.field.value) { success =>
                    AnalysisBuilderData.createEmptyAnalysis(nameComponent.field.value) { analysis =>
                        analysisId = analysis.id

                        lockAnalysisAndLoadPlugins({() =>
                            val view = new AnalysisEditorView(analysis, Some(nameComponent.field.value), None,"Create analysis")
                            view.visualizer.pluginInstanceRendered += {
                                e => instancesMap.put(e.target.pluginInstance.id, e.target)
                            }
                            view.render(parentElement)
                            view.setName(nameComponent.field.value)

                            view.runButton.mouseClicked += { args =>
                                window.location.href = "/analysis/" + analysisId
                                true
                            }

                            bindMenuEvents(view)
                            nameDialog.unblock()
                            nameDialog.destroy()
                        })
                    } {
                        error =>
                            nameDialog.unblock()
                            error match {
                                case rpc: ValidationException => {
                                    nameComponent.setError(rpc.message)
                                    false
                                }
                                case error: RpcException => {
                                    AlertModal.display("Validation failed", error.message)
                                    nameDialog.destroy()
                                }
                                case _ => fatalErrorHandler(_)
                            }
                        unblockPage()
                    }
                } { error =>
                    fatalErrorHandler(error)
                    unblockPage()
                }
                false
        }

        nameDialog.closing += {
            e =>
                window.location.href = "/dashboard"
                true
        }
    }

    private def mergeBranches(instances: mutable.HashMap[Int, PluginInstanceView], buffer: ArrayBuffer[PluginInstanceView],
        target: Plugin, view: AnalysisEditorView, mergeDialog: MergeAnalysisBranchesDialog) {

        var i = 0
        while (i < instances.size) {
            buffer.append(instances(i))
            instances(i).hideControls()
            branches -= instances(i)
            i = i + 1
        }

        mergeDialog.block("Merging branches ...")
        AnalysisBuilderData.createPluginInstance(target.id, analysisId) { createdInstance =>
            val mergeInstance = new EditablePluginInstanceView(createdInstance, buffer.asInstanceOf[Seq[PluginInstanceView]])
            view.visualizer.renderPluginInstanceView(mergeInstance)

            mergeInstance.connectButtonClicked += { clickedEvent =>
                connectPlugin(mergeInstance, view)
                false
            }

            mergeInstance.parameterValueChanged += onParameterValueChanged
            mergeInstance.deleteButtonClicked += onDeleteClick

            i = 0
            buffer.map { instance =>
                bind(instance, mergeInstance, i)
                i += 1
            }

            branches += mergeInstance
            mergeDialog.unblock()
            mergeDialog.destroy()
        } { e =>
            mergeDialog.unblock()
            mergeDialog.destroy()
            fatalErrorHandler(e)
        }
    }

    private def onDataSourceSelected(dataSource: DataSource, view: AnalysisEditorView) {
        blockPage("Making the data source available...")
        AnalysisBuilderData.cloneDataSourceAndBindToAnalysis(dataSource.id, analysisId) { pi =>
            val map = new mutable.HashMap[String, String]

            pi.parameterValues.foreach { paramValue =>
                map.put(paramValue.parameter.name, paramValue.value.toString)
            }

            val instance = new EditablePluginInstanceView(pi, List())

            branches.append(instance)
            view.visualizer.renderPluginInstanceView(instance)

            instance.connectButtonClicked += onConnectClicked(view)

            instance.parameterValueChanged += onParameterValueChanged
            instance.deleteButtonClicked += onDeleteClick

            unblockPage()
        } { err =>
            fatalErrorHandler(err)
            unblockPage()
        }
    }

    private def bindInstanceViewActions(instanceView: EditablePluginInstanceView, view: AnalysisEditorView) {
        instanceView.connectButtonClicked += { evt =>
            connectPlugin(evt.target, view)
            false
        }

        instanceView.parameterValueChanged += onParameterValueChanged
        instanceView.deleteButtonClicked += onDeleteClick
    }

    private def onInstanceCreated(createdInstance: PluginInstance, predecessor: Option[PluginInstanceView], view: AnalysisEditorView) {
        val instanceView = if (predecessor.isDefined) {
            new EditablePluginInstanceView(createdInstance, List(predecessor.get))
        } else {
            new EditablePluginInstanceView(createdInstance, List())
        }

        branches.append(instanceView)
        view.visualizer.renderPluginInstanceView(instanceView)
        bindInstanceViewActions(instanceView, view)

        predecessor.map { p =>
            branches -= p
            p.hideControls()
            bind(p, instanceView, 0)
        }
    }

    private def onPluginNameClicked(plugin: Plugin, predecessor: Option[PluginInstanceView], view: AnalysisEditorView) = {
        blockPage("Creating an instance of the plugin...")

        AnalysisBuilderData.createPluginInstance(plugin.id, analysisId) { createdInstance =>
            onInstanceCreated(createdInstance, predecessor, view)
            unblockPage()
        } { _ =>
            unblockPage()
        }
    }

    def connectPlugin(pluginInstance: PluginInstanceView, view: AnalysisEditorView): Unit = {
        val inner = pluginInstance

        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 1))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, Some(inner), view)
            dialog.destroy()
            false
        }
        dialog.render()
    }

    def onDeleteClick(eventArgs: EventArgs[PluginInstanceView]) {
        val instance = eventArgs.target
        blockPage("Deleting...")
        AnalysisBuilderData.deletePluginInstance(analysisId, instance.pluginInstance.id) { _ =>
            branches -= instance
            var i = 0
            while (i < instance.predecessors.size) {
                branches += instance.predecessors(i)
                instance.predecessors(i).showControls()
                i += 1
            }
            instance.destroy()
            unblockPage()
        } { _ =>
            unblockPage()
            AlertModal.display("Error when deleting", "The plugin could not be deleted.")
        }
    }

    def bind(a: PluginInstanceView, b: PluginInstanceView, inputIndex: Int) {
        blockPage("Working...")
        AnalysisBuilderData.saveBinding(analysisId, a.pluginInstance.id, b.pluginInstance.id, inputIndex) {
            _ => unblockPage()
        }{ e => unblockPage()
                fatalErrorHandler(e)
        }
    }

    protected def onParameterValueChanged(args: EventArgs[ParameterValue]) {
        val parameterInfo = args.target
        parameterInfo.control.isActive = true
        AnalysisBuilderData.setParameterValue(analysisId, parameterInfo.pluginInstanceId, parameterInfo.name, parameterInfo.value)
        { () =>
            parameterInfo.control.setOk()
            parameterInfo.control.isActive = false
        } { e =>
            e match {
                case ex: ValidationException => {
                    parameterInfo.control.setError("Wrong parameter value.")
                    parameterInfo.control.isActive = false
                }
                case _ => fatalErrorHandler(e)
            }
        }
    }

    protected def onConnectClicked(view: AnalysisEditorView): (EventArgs[PluginInstanceView]) => Unit = { evt =>
        connectPlugin(evt.target, view)
        false
    }

    protected def lockAnalysisAndLoadPlugins(callback: (() => Unit)) {
        AnalysisBuilderData.lockAnalysis(analysisId) { () =>
            AnalysisBuilderData.getPlugins() { plugins =>
                allPlugins = plugins

                AnalysisBuilderData.getDataSources() { sources =>
                    allSources = sources
                    callback()
                } {
                    error => fatalErrorHandler(error)
                }
            } {
                error => fatalErrorHandler(error)
            }

        } {
            error => fatalErrorHandler(error)
        }
    }

    protected def bindNameChanged(view: AnalysisEditorView) {
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
    }

    protected def bindDescriptionChanged(view: AnalysisEditorView) {
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
    }

    protected def bindAddPluginClicked(view: AnalysisEditorView) {
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
    }

    protected def bindAddDataSourceClicked(view: AnalysisEditorView) {
        view.addDataSourceLink.mouseClicked += { event =>
            val dialog = new DataSourceSelector("Select one of available data sources:", allSources)
            dialog.dataSourceSelected += { e =>
                onDataSourceSelected(e.target, view)
                dialog.destroy()
            }
            dialog.render()
            false
        }
    }

    protected def createAndShowMergeDialog(inputsCount: Int, targetPlugin: Plugin, view: AnalysisEditorView) {
        val mergeDialog = new MergeAnalysisBranchesDialog(branches, inputsCount)
        mergeDialog.confirming += { e =>
            val instances = mergeDialog.outputToInstance
            val buffer = new ArrayBuffer[PluginInstanceView]()

            if (mergeDialog.outputToInstance.size < inputsCount) {
                mergeDialog.destroy()
                AlertModal.display("Not enough inputs bound", "You need to bind all the inputs provided.")
            } else {
                mergeBranches(instances, buffer, targetPlugin, view, mergeDialog)
            }

            false
        }

        mergeDialog.render()
    }

    protected def bindChooseMergePluginDialog(dialog: PluginDialog, view: AnalysisEditorView) {
        dialog.pluginNameClicked += { evt =>
            dialog.destroy()

            val inputsCount = evt.target.inputCount
            if (inputsCount > branches.size) {
                AlertModal.display("The plugin can't be used", "The merge plugin has %d inputs, but only %d branches are available.".format(inputsCount, branches.size))
            } else {
                createAndShowMergeDialog(inputsCount, evt.target, view)
            }
            false
        }
        dialog.render()
    }

    protected def bindMergeBranchesClicked(view: AnalysisEditorView) {
        view.mergeBranches.mouseClicked += { event =>
            val dialog = new PluginDialog(allPlugins.filter(_.inputCount > 1))
            bindChooseMergePluginDialog(dialog, view)
            false
        }
    }

    protected def bindMenuEvents(view: AnalysisEditorView) {
        bindDescriptionChanged(view)
        bindNameChanged(view)
        bindAddPluginClicked(view)
        bindAddDataSourceClicked(view)
        bindMergeBranchesClicked(view)
    }
}
