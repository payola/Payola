package cz.payola.data.entities.plugins.parameters

import cz.payola.data.entities.plugins._
import cz.payola.data.PayolaDB

/**
  * This object converts [[cz.payola.domain.entities.plugins.parameters.FloatParameterValue]]
  * to [[cz.payola.data.entities.plugins.parameters.FloatParameterValue]]
  */
object FloatParameterValue {

    def apply(p: cz.payola.domain.entities.plugins.parameters.FloatParameterValue): FloatParameterValue = {
        p match {
            case param: FloatParameterValue => param
            case _ => {
                val parameter = FloatParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.FloatParameter])
                val parameterValue = new FloatParameterValue(p.id, parameter, p.value)

                parameter.associateParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class FloatParameterValue(
    override val id: String,
    param: FloatParameter,
    override var value: Float)
    extends cz.payola.domain.entities.plugins.parameters.FloatParameterValue(param, value)
    with ParameterValue[Float]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfFloatParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
