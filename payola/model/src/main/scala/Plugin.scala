package cz.payola.model

import cz.payola.model.parameter._
import collection.mutable.ArrayBuffer

class Plugin {
    val parameters: ArrayBuffer[Parameter[_]] = new ArrayBuffer[Parameter[_]]()
}
