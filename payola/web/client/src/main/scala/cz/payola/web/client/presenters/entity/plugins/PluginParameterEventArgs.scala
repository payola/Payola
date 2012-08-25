package cz.payola.web.client.presenters.entity.plugins

import cz.payola.web.client.events.EventArgs
import cz.payola.common.entities.plugins.Parameter

class PluginParameterEventArgs[A](target: A, val pluginParameter: Parameter[_])
    extends EventArgs[A](target)
