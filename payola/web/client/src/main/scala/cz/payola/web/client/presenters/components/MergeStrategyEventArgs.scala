package cz.payola.web.client.presenters.components

import scala.collection.mutable
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.todo.PluginInstance

class MergeStrategyEventArgs(target: mutable.HashMap[Int,PluginInstance]) extends EventArgs[mutable.HashMap[Int,PluginInstance]](target)
{
}
