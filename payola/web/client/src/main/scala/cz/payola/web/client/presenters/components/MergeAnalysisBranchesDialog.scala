package cz.payola.web.client.presenters.components

import s2js.adapters.browser._
import s2js.adapters.html
import s2js.compiler.javascript
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.entity.plugins.PluginInstanceView
import cz.payola.web.client.views.elements.Div

class MergeAnalysisBranchesDialog(instances: ArrayBuffer[PluginInstanceView], inputsCount: Int)
    extends Modal("Choose how you want to merge the branches")
{
    val outputToInstance = new HashMap[Int, PluginInstanceView]

    private val dragZone = new Div(List(),"droppable origin")


    var id = 1
    instances.map{instance =>
        val div = new Div(List(new Text(instance.pluginInstance.plugin.name)), "alert alert-danger col-lg-2 draggable")

        div.mouseMoved += { e =>
            instance.addCssClass("highlight")
            false
        }

        div.mouseOut += { e =>
            instance.removeCssClass("highlight")
            false
        }

        bindInstance(div.htmlElement, instance)
        div.render(dragZone.htmlElement)
        id += 1
    }

    val clear = new Div(List(),"clear")
    clear.render(dragZone.htmlElement)

    val dropZoneWrapper = new Div(List())

    var i = 0
    while (i < inputsCount)
    {
        val div = new Div(List(new Text("Input #"+i.toString())), "droppable well")
        bindIndex(div.htmlElement, i)
        div.render(dropZoneWrapper.htmlElement)
        i = i+1
    }

    override val body = List(dragZone, dropZoneWrapper)

    override def render(parent: html.Element = document.body) {
        super.render(parent)
        bindDragAndDrop()
    }

    @javascript(
        """
           jQuery(".draggable").draggable({revert: "invalid"});
           jQuery(".droppable").droppable({
                hoverClass: "ui-state-hover",
           });
           jQuery(".droppable.well").droppable({
                drop: function(event, ui) {
                    self.setInstance($(this).data("inputIndex"),$(ui.draggable).data("pluginInstance"));
                    $(this).droppable('option', 'accept', ui.draggable);
                },
                out: function(event, ui){
                    $(this).droppable('option', 'accept', '.draggable');
                }
           });
        """)
    def bindDragAndDrop() { }

    @javascript(""" jQuery(element).data("pluginInstance", instance); """)
    def bindInstance(element: html.Element, instance: PluginInstanceView) {}

    @javascript(""" jQuery(element).data("inputIndex", index); """)
    def bindIndex(element: html.Element, index: Int) { }

    def setInstance(index: Int, instance: PluginInstanceView) = {
        outputToInstance.put(index, instance)
    }
}
