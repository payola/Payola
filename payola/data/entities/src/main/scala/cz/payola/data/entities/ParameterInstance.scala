package cz.payola.data.entities

trait ParameterInstance[A] extends cz.payola.domain.entities.parameters.ParameterInstance[A]
{
    val parameterId: String = if (parameter == null) "" else parameter.id

    def pluginInstanceId: String
}
