package cz.payola.model

import cz.payola.model.parameter._
import collection.mutable.ArrayBuffer

class PluginInstance (val plugin: Plugin) {
    require(plugin != null, "Cannot create a plugin instance of a null plugin!")

    val parameters: ArrayBuffer[ParameterInstance[_]] = new ArrayBuffer[ParameterInstance[_]]()
}
