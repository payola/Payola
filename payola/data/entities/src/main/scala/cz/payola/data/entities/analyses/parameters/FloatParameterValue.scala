package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginInstance, ParameterValue}
import cz.payola.data.entities._

class FloatParameterValue(
    override val id: String,
    param: FloatParameter,
    override var value: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameterValue(param, value)
    with ParameterValue[Float]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfFloatParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
