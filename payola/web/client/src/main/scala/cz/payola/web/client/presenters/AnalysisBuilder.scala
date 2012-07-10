package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import cz.payola.web.client.presenters.components._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.compiler.javascript
import cz.payola.common.entities.Plugin
import s2js.adapters.js.browser.window
import cz.payola.web.client.events.ClickedEventArgs
import scala.collection.mutable.ArrayBuffer
import s2js.runtime.client.scala.collection.mutable.HashMap

class AnalysisBuilder(menuHolder: String, pluginsHolder: String, nameHolder: String)
{
    private var allPlugins : Seq[Plugin] = List()
    private var analysisId = ""

    AnalysisBuilderData.getPlugins(){ plugins => allPlugins = plugins}{ error => }
    AnalysisBuilderData.createEmptyAnalysis(){
        id =>
            analysisId = id
            AnalysisBuilderData.lockAnalysis(id)
    }{ error => }

    private var lanes = new ArrayBuffer[PluginInstance]

    private val menu = document.getElementById(menuHolder)
    private val pluginsHolderElement = document.getElementById(pluginsHolder)
    private val nameHolderElement = document.getElementById(nameHolder)

    private val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))
    private val addPluginLinkLi = new ListItem(List(addPluginLink))

    private val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))
    private val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    private val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))
    private val mergeBranchesLi = new ListItem(List(mergeBranches))

    private val name = new Input("name","",Some("Analysis name"),"span3")
    name.render(nameHolderElement)

    private var nameChangedTimeout : Option[Int] = None

    name.changed += { eventArgs =>
        if (nameChangedTimeout.isDefined){
            window.clearTimeout(nameChangedTimeout.get)
        }

        nameChangedTimeout = Some(window.setTimeout({
            AnalysisBuilderData.setAnalysisName(analysisId, eventArgs.target.value){ _ => () } { _ => () }
        }, 300))

        false
    }

    addPluginLinkLi.render(menu)
    addDataSourceLinkLi.render(menu)
    mergeBranchesLi.render(menu)

    addPluginLink.clicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 0))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, None)
            dialog.hide
            false
        }
        dialog.render(document.body)
        dialog.show

        false
    }

    mergeBranches.clicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount > 1))
        dialog.pluginNameClicked += { evt =>

            dialog.hide()

            val inputsCount = evt.target.inputCount
            if (inputsCount > lanes.size) {
                window.alert("The merge plugin has " + inputsCount.toString() + " inputs, but only " + lanes
                    .size + " branches are available.")
            } else {
                val mergeDialog = new MergeAnalysisBranchesDialog(lanes, inputsCount)
                mergeDialog.render(document.body)

                mergeDialog.mergeStrategyChosen += { event: MergeStrategyEventArgs =>
                    val instances = event.target

                    var i = 0
                    val buffer = new ArrayBuffer[PluginInstance]()

                    while (i < instances.size) {
                        buffer.append(instances(i))
                        instances(i).hideDeleteButton()
                        lanes -= instances(i)
                        i += 1
                    }

                    val mergeInstance = new PluginInstance(evt.target, buffer.asInstanceOf[Seq[PluginInstance]])
                    mergeInstance.render(pluginsHolderElement)

                    mergeInstance.connectButtonClicked += { clickedEvent =>
                        connectPlugin(mergeInstance)
                        false
                    }

                    mergeInstance.deleteButtonClicked += onDeleteClick

                    buffer.map { instance: Any =>
                        bind(instance.asInstanceOf[PluginInstance], mergeInstance)
                    }

                    lanes += mergeInstance

                    mergeDialog.hide()
                }

                mergeDialog.show()
            }

            false
        }
        dialog.render(document.body)
        dialog.show
        false
    }

    def onPluginNameClicked(plugin: Plugin, predecessor: Option[PluginInstance]) = {
        val instance = if (predecessor.isDefined) {
            new PluginInstance(plugin, List(predecessor.get))
        }else{
            new PluginInstance(plugin, List())
        }

        lanes.append(instance)

        instance.render(pluginsHolderElement)
        instance.connectButtonClicked += { evt =>
            connectPlugin(evt.target)
            false
        }

        instance.deleteButtonClicked += onDeleteClick

        predecessor.map{ p =>
            lanes -= p
            p.hideDeleteButton()
            bind(p, instance)
        }
    }

    def connectPlugin(pluginInstance: PluginInstance): Unit = {
        val inner = pluginInstance

        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 1))
        dialog.pluginNameClicked += { evtArgs =>
            onPluginNameClicked(evtArgs.target, Some(inner))
            dialog.hide
            false
        }
        dialog.render(document.body)
        dialog.show
    }

    def onDeleteClick = { eventArgs: ClickedEventArgs[PluginInstance] =>
        val instance = eventArgs.target
        lanes -= instance
        var i = 0
        while (i < instance.predecessors.size) {
            lanes += instance.predecessors(i)
            instance.predecessors(i).showDeleteButton()
            i += 1
        }
        instance.destroy()
        false
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
    def bind(a: PluginInstance, b: PluginInstance) = null
}
