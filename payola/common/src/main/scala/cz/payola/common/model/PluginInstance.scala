package cz.payola.common.model

trait PluginInstance extends ModelObject {
    val plugin: Plugin

    def allValues: List[ParameterInstance[_]]
    def hasSetValueForParameter(p: Parameter[_]): Boolean
    def setValueForParameter(p: Parameter[_], v: ParameterInstance[_])
    def valueForParameter(p: Parameter[_]): Option[ParameterInstance[_]]

}
