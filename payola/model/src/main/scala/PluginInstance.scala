package cz.payola.model

import collection.mutable.ArrayBuffer

class PluginInstance (val plugin: Plugin) {
    assert(plugin != null, "Cannot create a plugin instance of a null plugin!")

    val parameters: ArrayBuffer[ParameterInstance] = new ArrayBuffer[ParameterInstance]()
}
