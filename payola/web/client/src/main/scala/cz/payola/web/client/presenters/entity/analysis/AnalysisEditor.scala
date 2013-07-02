package cz.payola.web.client.presenters.entity.analysis

import cz.payola.web.shared.AnalysisBuilderData
import cz.payola.web.client.views.entity.analysis._
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.common.entities.Analysis
import cz.payola.common.ValidationException
import s2js.adapters.browser.`package`._
import cz.payola.web.client.views.entity.plugins.PluginInstanceViewFactory
import cz.payola.web.client.models.PrefixApplier

/**
 * A variant of AnalysisBuilder for editing an existing analysis. Overrides initialize method.
 * @param parentElementId ID of the DOM element to render views into
 * @param analysisIdParam ID of the edited analysis
 */
class AnalysisEditor(parentElementId: String, analysisIdParam: String)
    extends AnalysisBuilder(parentElementId)
{
    analysisId = analysisIdParam

    override def initialize() {
        blockPage("Loading analysis data...")
        prefixPresenter.initialize
        AnalysisBuilderData.getAnalysis(analysisId) { analysis =>

            instanceViewFactory = new PluginInstanceViewFactory(prefixPresenter.prefixApplier)

            lockAnalysisAndLoadPlugins({ () =>
                val view = new AnalysisEditorView(analysis, None, None, "Edit analysis", prefixPresenter.prefixApplier)
                view.visualizer.pluginInstanceRendered += { e => instancesMap.put(e.target.pluginInstance.id, e.target)}
                view.render(parentElement)
                bindParameterChangedEvent(view.visualizer)
                bindConnectButtonClickedEvent(view, analysis)
                bindDeleteButtonClickedEvent(view.visualizer)
                constructBranches(analysis)
                bindMenuEvents(view, analysis)

                view.runButton.mouseClicked += { args =>
                    window.location.href = "/analysis/" + analysisId
                    true
                }

                unblockPage()
            })
            true
        } { error => fatalErrorHandler(error)}
    }

    private def constructBranches(analysis: Analysis) {
        val targets = analysis.pluginInstances.filterNot { pi =>
            analysis.pluginInstanceBindings.find(_.sourcePluginInstance.id == pi.id).isDefined
        }.map { pi => instancesMap.get(pi.id).get}

        targets.foreach(branches.append(_))
    }

    private def bindParameterChangedEvent(visualizer: EditableAnalysisVisualizer) {
        visualizer.parameterValueChanged += { e =>
            val pv = e.target
            pv.control.isActive = true
            storeParameterValueToServer(pv)
        }
    }

    private def bindConnectButtonClickedEvent(view: AnalysisEditorView, analysis: Analysis) {
        view.visualizer.connectButtonClicked += onConnectClicked(view, analysis)
    }

    private def bindDeleteButtonClickedEvent(visualizer: EditableAnalysisVisualizer) {
        visualizer.deleteButtonClicked += onDeleteClick
    }

    private def storeParameterValueToServer(pv: ParameterValue) {
        parameterChangedServerCall(pv)
    }

    private def parameterChangedServerCall(pv: ParameterValue) {
        AnalysisBuilderData.setParameterValue(analysisId, pv.pluginInstanceId, pv.name, pv.value) { () =>
            pv.control.isActive = false
            pv.control.setOk()
        } { error =>
            error match {
                case e: ValidationException => {
                    pv.control.isActive = false
                    pv.control.setError("Invalid value")
                }
                case _ => fatalErrorHandler(error)
            }
        }
    }
}
