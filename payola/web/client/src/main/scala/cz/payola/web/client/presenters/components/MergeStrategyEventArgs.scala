package cz.payola.web.client.presenters.components

import cz.payola.web.client.events.EventArgs
import s2js.runtime.client.scala.collection.Map
import cz.payola.web.client.mvvm.element.extensions.Payola.PluginInstance

class MergeStrategyEventArgs(target: Map[Int,PluginInstance]) extends EventArgs[Map[Int,PluginInstance]](target)
{
}
