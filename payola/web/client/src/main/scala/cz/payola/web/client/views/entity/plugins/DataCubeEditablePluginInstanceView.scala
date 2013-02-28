package cz.payola.web.client.views.entity.plugins

import cz.payola.common.entities.plugins.parameters._
import cz.payola.web.client.presenters.models.ParameterValue
import scala.collection._
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.shared.AnalysisRunner
import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.entity.analysis.AnalysisRunnerView
import s2js.adapters.browser.`package`._
import cz.payola.common._
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationSuccess
import cz.payola.common.EvaluationInProgress
import cz.payola.web.client.views.algebra.Point2D
import cz.payola.web.client.views.GraphView
import cz.payola.web.client.views.graph.SimpleGraphView
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationSuccess
import scala._
import cz.payola.common.EvaluationInProgress
import scala.collection.mutable.ArrayBuffer
import scala.Some
import cz.payola.common.EvaluationInProgress
import cz.payola.common.EvaluationError
import scala.collection.Seq
import cz.payola.common.EvaluationSuccess
import s2js.compiler.javascript

class DataCubeEditablePluginInstanceView(analysis: Analysis, pluginInst: PluginInstance,
    predecessors: Seq[PluginInstanceView] = List())
    extends EditablePluginInstanceView(pluginInst, predecessors)
{

    private def name = {
        val nameParts = pluginInstance.plugin.name.split("#")
        if (nameParts.length > 1) nameParts(1) else pluginInstance.plugin.name
    }

    override def getHeading: Seq[View] = List(new Heading(List(new Text("DataCube Vocabulary")), 3),
        new Paragraph(List(new Text(name))))

    override def getParameterViews = {
        // THE FIRST PARAMETER is used as the pattern carrier
        val param = getPlugin.parameters.head

        val input = new TextArea(param.id, "", "", "tiny datacube")
        val control = new InputControl[TextArea]("", input, None)

        val button = new Button(new Text("Choose pattern ..."), "datacube", new Icon(Icon.hand_up))
        button.mouseClicked += {
            evt =>
                block("Making data preview...")
                AnalysisRunner.createPartialAnalysis(analysis.id, pluginInstance.id) {
                    analysisId =>
                        AnalysisRunner.runAnalysisById(analysisId, 30, "") {
                            evalId =>
                                schedulePolling(evalId, {
                                    args =>

                                        val query = "CONSTRUCT { " +
                                            "[] a <http://purl.org/linked-data/cube#Observation> ; " +
                                            "<http://purl.org/linked-data/cube#dataSet> <http://live.payola" +
                                             ".cz/analysis/UUID> ; " +
                                            getPattern(args.target.getSignificantVertices).mkString(" ; ") + " . " +
                                            "} where { " +
                                            "    { " +
                                            "        SELECT DISTINCT " + args.target.getSignificantVertices
                                            .mkString(" ") + " { " +
                                            args.target.getPattern +
                                            "        } LIMIT 10 " +
                                            "    } " +
                                            "} "

                                        input.updateValue(query)
                                        parameterValueChanged.triggerDirectly(
                                            new ParameterValue(getId, param.id, param.name, query, control))
                                })
                        } {
                            error =>
                        }
                } {
                    error =>
                }

                false
        }

        val div = new Div(List(button, input))
        List(div)
    }

    def getPattern(vertices: ArrayBuffer[String]): Seq[String] = {
        var i = 0
        getPlugin.parameters.map {
            p =>
                val str = "<" + p.name + "> " + vertices.apply(i)
                i += 1
                str
        }
    }

    private def schedulePolling(evaluationId: String, callback: (EventArgs[SimpleGraphView] => Unit)) {
        window.setTimeout(() => {
            pollingHandler(evaluationId, callback)
        }, 500)
    }

    private def pollingHandler(evaluationId: String, callback: (EventArgs[SimpleGraphView] => Unit)) {
        AnalysisRunner.getEvaluationState(evaluationId) {
            state =>
                state match {
                    case s: EvaluationError =>
                    case s: EvaluationSuccess => {

                        val messages = getPlugin.parameters.map { p =>
                            new Div(List(
                                new Text("Please, select a vertex corresponding to the "+p.name+" component:")
                            ), "message")
                        }
                        val infoBar = new Div(messages,"datacube-infobar")
                        val graphPlaceholder = new Div(List(infoBar), "datacube-preview")

                        val modal = new Modal("Analysis preview", List(graphPlaceholder), None, None, true,
                            "datacube-preview-dialog")
                        modal.render()

                        val view = new SimpleGraphView(graphPlaceholder, getPlugin.parameters.size)
                        view.update(Some(s.outputGraph), None)

                        view.patternUpdated += {
                            args =>
                                callback(args)
                                graphPlaceholder.destroy()
                                modal.destroy()
                        }

                        unblock()
                    }
                    case s: EvaluationTimeout =>
                }

                if (state.isInstanceOf[EvaluationInProgress]) {
                    schedulePolling(evaluationId, callback)
                }
        } {
            error =>
        }
    }
}
