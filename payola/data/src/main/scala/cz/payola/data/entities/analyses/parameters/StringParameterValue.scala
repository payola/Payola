package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.PayolaDB

object StringParameterValue {

    def apply(p: cz.payola.domain.entities.analyses.parameters.StringParameterValue): StringParameterValue = {
        p match {
            case param: StringParameterValue => param
            case _ => {
                val parameter = StringParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter])
                val parameterValue = new StringParameterValue(p.id, parameter, p.value)

                parameter.registerParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class StringParameterValue(
    override val id: String,
    param: StringParameter,
    override var value: String)
    extends cz.payola.domain.entities.analyses.parameters.StringParameterValue(param, value)
    with ParameterValue[String]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfStringParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
