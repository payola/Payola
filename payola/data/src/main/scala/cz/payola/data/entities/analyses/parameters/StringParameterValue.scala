package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.PayolaDB

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
