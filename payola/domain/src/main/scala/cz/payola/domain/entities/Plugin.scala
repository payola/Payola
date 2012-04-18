package cz.payola.domain.entities

import collection.immutable

class Plugin(protected var _name: String, protected val _parameters: immutable.Seq[Plugin#ParameterType])
    extends Entity with NamedEntity with ShareableEntity with cz.payola.common.entities.Plugin
{
    type ParameterType = Parameter[_]

    protected var _isPublic = false

    /**
      * Returns a new instance of the plugin with all parameter instances set to default values.
      */
    def createInstance: PluginInstance = {
        new PluginInstance(this, _parameters.map(_.createInstance(None)))
    }
}
