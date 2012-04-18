package cz.payola.domain.entities

import scala.collection.immutable

class PluginInstance(protected val _plugin: Plugin,
    protected val _parameterInstances: immutable.Seq[PluginInstance#ParameterInstanceType])
    extends Entity with cz.payola.common.entities.PluginInstance
{
    require(parameterInstances.map(_.parameter).sortBy(_.name) == plugin.parameters.sortBy(_.name),
        "The instance doesn't contain parameter instances corresponding to the plugin.")
    //TODO: cannot create DB Schema with this check
    //require(plugin != null, "Cannot create a plugin instance of a null plugin!")

    type PluginType = Plugin

    type ParameterInstanceType = ParameterInstance[_]

    def getParameterInstance[A](parameter: Parameter[A]): ParameterInstance[_] = {
        require(plugin.parameters.contains(parameter), "The parameter doesn't belong to the plugin corresponding " +
            "to this instance.")
        parameterInstances.find(_.parameter == parameter).get
    }
}
