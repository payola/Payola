package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.Event
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import scala.collection.mutable

class MergeStrategyEvent extends Event[mutable.HashMap[Int,PluginInstance], MergeStrategyEventArgs, Unit]
{
    protected def handlerResultsFolder(stackTop: Unit, currentHandlerResult: Unit) = {

    }

    protected def resultsFolderInitializer = {}
}
