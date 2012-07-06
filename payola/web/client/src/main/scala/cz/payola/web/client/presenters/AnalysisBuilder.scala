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
import s2js.runtime.client.scala.collection.mutable.ArrayBuffer

class AnalysisBuilder(menuHolder: String, pluginsHolder: String)
{
    val allPlugins: Seq[Plugin] = AnalysisBuilderData.getPlugins()

    var lanes = new ArrayBuffer

    val menu = document.getElementById(menuHolder)

    val pluginsHolderElement = document.getElementById(pluginsHolder)

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    val addPluginLinkLi = new ListItem(List(addPluginLink))

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))

    val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    val mergeBranchesLi = new ListItem(List(mergeBranches))

    addPluginLinkLi.render(menu)
    addDataSourceLinkLi.render(menu)
    mergeBranchesLi.render(menu)

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

            instance.deleteButtonClicked += { evt =>
                lanes -= instance
                var i = 0
                while (i < instance.predecessors.size) {
                    lanes += instance.predecessors(i)
                    i += 1
                }
                instance.destroy()
                false
            }

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
                    val buffer = ArrayBuffer.empty

                    while (i < instances.size) {
                        buffer += instances(i)
                        lanes -= instances(i)
                        i += 1
                    }

                    val mergeInstance = new PluginInstance(evt.target, buffer.asInstanceOf[Seq[PluginInstance]])
                    mergeInstance.render(pluginsHolderElement)

                    mergeInstance.connectButtonClicked += { clickedEvent =>
                        connectPlugin(mergeInstance)
                        false
                    }

                    mergeInstance.deleteButtonClicked += { evt =>
                        lanes -= mergeInstance
                        var i = 0
                        while (i < mergeInstance.predecessors.size) {
                            lanes += mergeInstance.predecessors(i)
                            i += 1
                        }
                        mergeInstance.destroy()
                        false
                    }

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

            instance.render(pluginsHolderElement)

            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            instance.deleteButtonClicked += { evt =>
                lanes -= instance
                var i = 0
                while (i < instance.predecessors.size) {
                    lanes += instance.predecessors(i)
                    i += 1
                }
                instance.destroy()
                false
            }

            bind(inner, instance)

            dialog.hide
            false
        }
        dialog.render(document.body)
        dialog.show
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
