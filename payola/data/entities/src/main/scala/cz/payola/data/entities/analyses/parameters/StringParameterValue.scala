package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.entities.PayolaDB

object StringParameterValue {

    def apply(p: cz.payola.domain.entities.analyses.parameters.StringParameterValue): StringParameterValue = {
        val parameter = StringParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.StringParameter])
        new StringParameterValue(p.id, parameter, p.value)
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
