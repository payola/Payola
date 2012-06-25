package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.PayolaDB

object IntParameterValue {

    def apply(p: cz.payola.domain.entities.plugins.parameters.IntParameterValue): IntParameterValue = {
        p match {
            case param: IntParameterValue => param
            case _ => {
                val parameter = IntParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.IntParameter])
                val parameterValue = new IntParameterValue(p.id, parameter, p.value)

                parameter.registerParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class IntParameterValue(
    override val id: String,
    param: IntParameter,
    override var value: Int)
    extends cz.payola.domain.entities.plugins.parameters.IntParameterValue(param, value)
    with ParameterValue[Int]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfIntParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
