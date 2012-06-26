package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element.extensions.Bootstrap._
import cz.payola.web.client.mvvm.element._
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import cz.payola.web.client.presenters.components.PluginDialog
import cz.payola.web.shared.AnalysisBuilderData
import s2js.compiler.javascript

class AnalysisBuilder(menuHolder: String, pluginsHolder: String)
{
    val menu = document.getElementById(menuHolder)
    val pluginsHolderElement = document.getElementById(pluginsHolder)

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))
    val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    addDataSourceLinkLi.render(menu)

    def connectPlugin(pluginInstance: PluginInstance) : Unit =  {
        val inner = pluginInstance

        val dialog = new PluginDialog(AnalysisBuilderData.getPlugins)
        dialog.pluginNameClicked += { evt =>
            val instance = new PluginInstance(evt.target.name, Some(inner))
            instance.render(pluginsHolderElement)

            instance.connectButtonClicked += { evt =>
                connectPlugin(evt.target)
                false
            }

            bind(inner, instance)

            dialog.hide
            false
        }
        dialog.render(document.body)
        dialog.show
    }

    addDataSourceLink.clicked += {event =>

        val instance = new PluginInstance("SPARQL Endpoint", None)
        instance.render(pluginsHolderElement)

        instance.connectButtonClicked += { evt =>
            connectPlugin(evt.target)
            false
        }

        false
    }

    @javascript(
        """
          var settings = {
                            PaintStyle:{ lineWidth:2, strokeStyle:"#BCE8F1", outlineColor:"#3A87AD", outlineWidth:1 },
                            Connector:[ "Flowchart", { stub: 1 } ],
                            Endpoint:[ "Dot", { radius:4 } ],
                            EndpointStyle : { fillStyle: "#3A87AD"  },
                            Anchor : [ "TopCenter", "BottomCenter" ]
                       };

          var instance = jsPlumb.getInstance(settings);
          instance.connect({ source:a.alertDiv.div, target:b.alertDiv.div, scope:"someScope" });
        """)
    def bind(a: PluginInstance, b: PluginInstance) = null
}
