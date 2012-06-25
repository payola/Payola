package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.PayolaDB

object BooleanParameterValue {

    def apply(p: cz.payola.domain.entities.plugins.parameters.BooleanParameterValue): BooleanParameterValue = {
        p match {
            case param: BooleanParameterValue => param
            case _ => {
                val parameter = BooleanParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.BooleanParameter])
                val parameterValue = new BooleanParameterValue(p.id, parameter, p.value)

                parameter.registerParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class BooleanParameterValue(
    override val id: String,
    param: BooleanParameter,
    override var value: Boolean)
    extends cz.payola.domain.entities.plugins.parameters.BooleanParameterValue(param, value)
    with ParameterValue[Boolean]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfBooleanParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
