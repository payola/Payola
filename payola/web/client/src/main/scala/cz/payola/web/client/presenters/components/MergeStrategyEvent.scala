package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.Event
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import s2js.runtime.client.scala.collection.Map

class MergeStrategyEvent extends Event[Map[Int,PluginInstance], MergeStrategyEventArgs, Unit]
{
    protected def handlerResultsFolder(stackTop: Unit, currentHandlerResult: Unit) = {

    }

    protected def resultsFolderInitializer = {}
}
