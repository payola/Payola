package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.EventArgs
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance
import scala.collection.mutable

class MergeStrategyEventArgs(target: mutable.HashMap[Int,PluginInstance]) extends EventArgs[mutable.HashMap[Int,PluginInstance]](target)
{
}
