package cz.payola.web.client.presenters.components

import cz.payola.web.client.views._
import cz.payola.web.client.views.Component
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import s2js.compiler.javascript
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.extensions.bootstrap.Modal

class MergeAnalysisBranchesDialog(instances: ArrayBuffer[PluginInstance], inputsCount: Int) extends Component
{
    val mergeStrategyChosen = new MergeStrategyEvent()
    val outputToInstance = new HashMap[Int, PluginInstance]

    private val dragZone = new Div(List(),"droppable origin")

    instances.map{instance:Any =>
        val pluginInstance = instance.asInstanceOf[PluginInstance]

        val div = new Div(List(new Text(pluginInstance.plugin.name)), "alert alert-danger span2 draggable")
        bindInstance(div.getDomElement, pluginInstance)
        div.render(dragZone.getDomElement)
    }

    val clear = new Div(List(),"clear")
    clear.render(dragZone.getDomElement)

    val dropZoneWrapper = new Div(List())

    var i = 0
    while (i < inputsCount)
    {
        val div = new Div(List(new Text("Input #"+i.toString())), "droppable well")
        bindIndex(div.getDomElement, i)
        div.render(dropZoneWrapper.getDomElement)
        i = i+1
    }

    private val dialog = new Modal("Choose how you want to merge the branches", List(dragZone, dropZoneWrapper))

    def render(parent: Element = document.body) = {
        dialog.render(parent)
        bindDragAndDrop()
    }

    def show() = dialog.show

    def hide() = dialog.hide

    def getDomElement : Element = {
        dialog.getDomElement
    }

    dialog.saved += { event =>
        mergeStrategyChosen.trigger(new MergeStrategyEventArgs(outputToInstance))
        false
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
    def bindDragAndDrop() = {}

    @javascript(""" jQuery(element).data("pluginInstance", instance); """)
    def bindInstance(element: Element, instance: PluginInstance) = {}

    @javascript(""" jQuery(element).data("inputIndex", index); """)
    def bindIndex(element: Element, index: Int) = {}

    def setInstance(index: Int, instance: PluginInstance) = {
        outputToInstance.put(index, instance)
    }
}
