package cz.payola.web.client.presenters

import cz.payola.web.shared.AnalysisBuilderData
import cz.payola.web.client.views.entity.analysis._
import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.common.entities.Analysis

class AnalysisEditor(parentElementId: String, analysisIdParam: String)
    extends AnalysisBuilder(parentElementId)
{
    analysisId = analysisIdParam

    override def initialize() {
        AnalysisBuilderData.getAnalysis(analysisId) { analysis =>

            lockAnalysisAndLoadPlugins()
            val view = new AnalysisEditorView(analysis)
            view.visualiser.pluginInstanceRendered += { e => instancesMap.put(e.target.id, e.target)}
            view.render(parentElement)
            view.nameControl.input.value = analysis.name
            view.description.input.value = analysis.description
            bindParameterChangedEvent(view.visualiser)
            bindConnectButtonClickedEvent(view)
            bindDeleteButtonClickedEvent(view.visualiser)
            constructBranches(analysis)
            bindMenuEvents(view)

            true
        } { error => fatalErrorHandler(error) }
    }

    private def constructBranches(analysis: Analysis){
        val targets = analysis.pluginInstances.filterNot{ pi =>
            analysis.pluginInstanceBindings.find(_.sourcePluginInstance.id == pi.id).isDefined
        }.map{ pi => instancesMap.get(pi.id).get }

        targets.foreach(branches.append(_))
    }

    private def bindParameterChangedEvent(visualiser: EditableAnalysisVisualizer){
        visualiser.parameterValueChanged += { e =>
            val pv = e.target
            pv.control.setIsActive()
            storeParameterValueToServer(pv)
        }
    }

    private def bindConnectButtonClickedEvent(view: AnalysisEditorView){
        view.visualiser.connectButtonClicked += onConnectClicked(view)
    }

    private def bindDeleteButtonClickedEvent(visualiser: EditableAnalysisVisualizer){
        visualiser.deleteButtonClicked += onDeleteClick
    }

    private def storeParameterValueToServer(pv: ParameterValue) {
        val key = getParameterValueId(pv)
        clearTimeOutIfSet(key)
        setTimeout(key, parameterChangedServerCall(pv))
    }

    private def getParameterValueId(pv: ParameterValue): String = {
        pv.pluginInstanceId + "_" + pv.parameterId
    }

    private def parameterChangedServerCall(pv: ParameterValue) : (() => Unit) = {
        () => AnalysisBuilderData.setParameterValue(analysisId,pv.pluginInstanceId,pv.name, pv.value){ success =>
            pv.control.setIsActive(false)
            pv.control.setOk()
        }{ error =>
            pv.control.setIsActive(false)
            pv.control.setError("Invalid value")
        }
    }
}
