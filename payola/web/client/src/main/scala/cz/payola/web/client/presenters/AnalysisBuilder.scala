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

class AnalysisBuilder(menuHolder: String, pluginsHolder: String, nameHolder: String, saveHolder: String)
{
    private val allPlugins: Seq[Plugin] = AnalysisBuilderData.getPlugins()
    private var lanes = new ArrayBuffer[PluginInstance]
    private val htmlIdToinstanceId = new HashMap[String, String]

    private val menu = document.getElementById(menuHolder)
    private val pluginsHolderElement = document.getElementById(pluginsHolder)
    private val nameHolderElement = document.getElementById(nameHolder)
    private val saveHolderElement = document.getElementById(saveHolder)

    private val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))
    private val addPluginLinkLi = new ListItem(List(addPluginLink))

    private val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))
    private val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    private val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))
    private val mergeBranchesLi = new ListItem(List(mergeBranches))

    private val name = new Input("name","",Some("Analysis name"),"span3")
    name.render(nameHolderElement)

    private val save = new Button("Save","btn-primary")
    save.render(saveHolderElement)

    addPluginLinkLi.render(menu)
    addDataSourceLinkLi.render(menu)
    mergeBranchesLi.render(menu)

    save.clicked += { eventArgs =>
        savePluginInstances(lanes)
        //saveBindings(getBindings())
        false
    }

    def saveBindings(bindings: Seq[Tuple2[String, String]]) = {

    }

    def savePluginInstances(instances: Seq[PluginInstance]) : Unit = {
        var i = 0
        while (i < instances.size){
            val instance = instances(i)
            savePluginInstances(instance.predecessors)
            val instanceId = AnalysisBuilderData.createInstance(instance.plugin.id, getParamsValues(instance))
            htmlIdToinstanceId.put(instance.getPluginElement.getAttribute("id"),instanceId)

            i += 1
        }
    }

    def getParamsValues(instance: PluginInstance) = {
        val buff = new ArrayBuffer[String]()

        var i = 0
        while (i < instance.plugin.parameters.size){
            buff.append(instance.getParamValue(i))
            i+=1
        }

        buff
    }

    addPluginLink.clicked += { event =>
        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 0))
        dialog.pluginNameClicked += { evt =>
            val instance = new PluginInstance(evt.target)
            lanes.append(instance)

            instance.render(pluginsHolderElement)

            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            instance.deleteButtonClicked += onDeleteClick

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

    def connectPlugin(pluginInstance: PluginInstance): Unit = {
        val inner = pluginInstance

        val dialog = new PluginDialog(allPlugins.filter(_.inputCount == 1))
        dialog.pluginNameClicked += { evt =>
            val instance = new PluginInstance(evt.target, List(inner))
            lanes.append(instance)
            lanes -= inner
            inner.hideDeleteButton()

            instance.render(pluginsHolderElement)

            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            instance.deleteButtonClicked += onDeleteClick

            bind(inner, instance)

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

    def saveBinding(source: String, target: String) = {
        val sourceId = htmlIdToinstanceId(source)
        val targetId = htmlIdToinstanceId(target)

        AnalysisBuilderData.saveBinding(sourceId, targetId)
    }

    @javascript(""" var connections = jsPlumb.getAllConnections().jsPlumb_DefaultScope; for (var k in connections) { self.saveBinding(connections[k].sourceId,connections[k].targetId); }; """)
    def saveBindings = {}
}
