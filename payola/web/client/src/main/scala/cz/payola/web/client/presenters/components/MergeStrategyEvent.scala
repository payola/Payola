package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.Event
import cz.payola.web.client.views.entity.plugins.PluginInstanceView
import scala.collection.mutable

class MergeStrategyEvent extends Event[mutable.HashMap[Int,PluginInstanceView], MergeStrategyEventArgs, Unit]
{
    protected def resultsFolderReducer(stackTop: Unit, currentHandlerResult: Unit) = {

    }

    protected def resultsFolderInitializer = {}
}
