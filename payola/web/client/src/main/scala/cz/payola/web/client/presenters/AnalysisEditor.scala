package cz.payola.web.client.presenters

import cz.payola.web.shared.AnalysisBuilderData
import cz.payola.web.client.views.entity.analysis.AnalysisVisualizer
import s2js.adapters.js.browser.window

class AnalysisEditor(parentElementId: String, analysisIdParam: String)
    extends AnalysisBuilder(parentElementId)
{
    analysisId = analysisIdParam

    override def initialize() {
        AnalysisBuilderData.getAnalysis(analysisId) { analysis =>

            lockAnalysisAndLoadPlugins()

            view.render(parentElement)
            view.nameControl.input.value = analysis.name
            view.description.input.value = analysis.description

            val visualiser = new AnalysisVisualizer(analysis, true)
            visualiser.render(view.analysisCanvas.domElement)

            visualiser.parameterValueChanged += { e =>
                val pv = e.target
                pv.control.setIsActive()

                if (timeoutMap.get(pv.pluginInstanceId+"_"+pv.parameterId).isDefined)
                {
                    window.clearTimeout(timeoutMap.get(pv.pluginInstanceId+"_"+pv.parameterId).get)
                }

                timeoutMap.put(pv.pluginInstanceId+"_"+pv.parameterId, window.setTimeout({ () =>
                    AnalysisBuilderData.setParameterValue(analysisId,pv.pluginInstanceId,pv.name, pv.value){ success =>
                        pv.control.setIsActive(false)
                        pv.control.setOk()
                    }{ error =>
                        pv.control.setIsActive(false)
                        pv.control.setError("Invalid value")
                    }
                }, saveAsYouTypeTimeout))
            }

            true
        } { error => fatalErrorHandler(error) }
    }


}
