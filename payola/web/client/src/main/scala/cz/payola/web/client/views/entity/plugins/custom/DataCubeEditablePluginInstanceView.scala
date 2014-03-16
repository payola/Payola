package cz.payola.web.client.views.entity.plugins.custom

import cz.payola.web.client.presenters.models.ParameterValue
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.View
import cz.payola.common.entities.plugins._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.shared.AnalysisRunner
import cz.payola.common.entities.Analysis
import cz.payola.common._
import cz.payola.web.client.views.graph.SimpleGraphView
import scala._
import scala.collection.mutable.ArrayBuffer
import scala.collection.Seq
import cz.payola.web.client.views.entity.plugins._
import scala.Some
import cz.payola.common.EvaluationInProgress
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationSuccess
import s2js.adapters.browser._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.common.entities.plugins.parameters.StringParameter
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import s2js.compiler.javascript
import cz.payola.web.shared.transformators._
import cz.payola.common.EvaluationInProgress
import cz.payola.common.EvaluationError
import cz.payola.common.EvaluationCompleted

/**
 * DataCube Editable plugin instance visualization
 * @param pluginInst plugin instance to visualize
 * @param predecessors
 * @author Jiri Helmich
 */
class DataCubeEditablePluginInstanceView(analysis: Analysis, pluginInst: PluginInstance,
    predecessors: Seq[PluginInstanceView] = List())
    extends EditablePluginInstanceView(pluginInst, predecessors, new PrefixApplier())
{
    private def name = {
        val nameParts = pluginInstance.plugin.name.split("#")
        if (nameParts.length > 1) nameParts(1) else pluginInstance.plugin.name
    }

    override def getHeading: Seq[View] = List(new Heading(List(new Text("DataCube Vocabulary")), 3),
        new Paragraph(List(new Text(name))))

    /**
     * Custom parameter views, autosafe for textarea, pattern selection controller
     * @return
     */
    override def getParameterViews = {
        val param = getPlugin.parameters.sortWith(_.ordering.getOrElse(9999) < _.ordering.getOrElse(9999)).head

        val initValue = pluginInstance.getParameterValue(param.name).map {p =>
            p.value.toString
        }.getOrElse("")

        val input = new TextArea(param.id, initValue, "", "tiny datacube")
        val control = new InputControl[TextArea]("Transformation query", input, None, None)
        control.delayedChanged += { e =>
            parameterValueChanged.triggerDirectly(
                new ParameterValue(getId, param.id, param.name, input.value, control))
        }

        val limitInput = new NumericInput("limitCount", 20, "", "")
        val limitControl = new InputControl[NumericInput]("Preview size", limitInput, None, None)

        val button = new Button(new Text("Choose pattern ..."), "datacube", new Icon(Icon.hand_up))
        button.mouseClicked += {
            evt =>
                View.blockPage("Making data preview...")

                val limitCount = limitInput.value

                // create partial analysis for preview, append limit, set timeout to 30 sec.
                AnalysisRunner.createPartialAnalysis(analysis.id, pluginInstance.id, limitCount) {
                    analysisId =>
                        AnalysisRunner.runAnalysisById(analysisId, "") {    // run the partial analysis
                            evalId =>
                                schedulePolling(evalId, {                       // on evaluation success callback - pattern done
                                    args =>

                                        val query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>   CONSTRUCT { " +
                                            "[] a <http://purl.org/linked-data/cube#Observation> ; " +
                                            "<http://purl.org/linked-data/cube#dataSet> <http://live.payola.cz/analysis/"+analysis.id+"> ; " +
                                            getPattern(args.target.getSignificantVertices).mkString(" ; ") + " . " +
                                            args.target.getSignificantVertices.map{v => v+" rdfs:label "+v.replace("?","?l")+" ."}.mkString(" ")+
                                            "} WHERE { " +
                                            "    { " +
                                            "        SELECT DISTINCT " + args.target.getSignificantVertices
                                            .mkString(" ") + " { " +
                                            args.target.getPattern +
                                            "        } " +
                                            "    } " +
                                            args.target.getSignificantVertices.map{v => " OPTIONAL { "+v+" rdfs:label "+v.replace("?","?l")+" . }"}.mkString(" ")+
                                            "} "

                                        input.updateValue(query)
                                        parameterValueChanged.triggerDirectly(
                                            new ParameterValue(getId, param.id, param.name, query, control))
                                })
                        } {
                            error => {
                                View.unblockPage()
                                AlertModal.display("Error", "An error occured.")
                            }
                        }
                } {
                    error =>
                }

                false
        }

        val div = new Div(List(button, control, limitControl))
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

    /**
     * Analysis preview handler
     * @param evaluationId
     * @param callback
     */
    private def pollingHandler(evaluationId: String, callback: (EventArgs[SimpleGraphView] => Unit)) {
        AnalysisRunner.getEvaluationState(evaluationId, analysis.id, "") {
            state =>
                state match {
                    case s: EvaluationError => {
                        View.unblockPage()
                        AlertModal.display("Error", "An error occured.")
                    }
                    case s: EvaluationCompleted => {

                        IdentityTransformator.transform(evaluationId){ g =>
                            val messages = getPlugin.parameters.map {
                                p =>
                                    new Div(List(
                                        new Text("Please, select a vertex corresponding to the " + p.defaultValue.toString+" ("+p.name + ") component:")
                                    ), "message")
                            }
                            val infoBar = new Div(messages, "datacube-infobar")
                            val graphPlaceholder = new Div(List(infoBar), "datacube-preview")

                            val modal = new Modal("Analysis preview", List(graphPlaceholder), None, None, true,
                                "preview-dialog")
                            modal.render()

                            val view = new SimpleGraphView(graphPlaceholder, getPlugin.parameters.size)
                            view.update(g, None, None)

                            view.patternUpdated += {
                                args =>
                                    callback(args)
                                    graphPlaceholder.destroy()
                                    modal.destroy()
                            }
                        }{ e => }

                        View.unblockPage()
                    }
                    case s: EvaluationTimeout => {
                        View.unblockPage()
                        AlertModal.display("Error", "Timeout occured.")
                    }
                }

                if (state.isInstanceOf[EvaluationInProgress]) {
                    schedulePolling(evaluationId, callback)
                }
        } {
            error =>
        }
    }
}