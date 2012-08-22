package cz.payola.web.client.presenters.components

import scala.collection.mutable
import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.views.entity.plugins.PluginInstanceView

class MergeStrategyEventArgs(target: mutable.HashMap[Int,PluginInstanceView]) extends EventArgs[mutable.HashMap[Int,PluginInstanceView]](target)
{
}
