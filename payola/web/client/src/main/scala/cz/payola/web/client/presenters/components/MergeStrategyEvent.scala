package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.Event
import cz.payola.web.client.views.PluginInstance
import scala.collection.mutable

class MergeStrategyEvent extends Event[mutable.HashMap[Int,PluginInstance], MergeStrategyEventArgs, Unit]
{
    protected def resultsFolderReducer(stackTop: Unit, currentHandlerResult: Unit) = {

    }

    protected def resultsFolderInitializer = {}
}
