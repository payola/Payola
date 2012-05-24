package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses._
import cz.payola.data.PayolaDB

class BooleanParameterValue(
    override val id: String,
    param: BooleanParameter,
    override var value: Boolean)
    extends cz.payola.domain.entities.analyses.parameters.BooleanParameterValue(param, value)
    with ParameterValue[Boolean]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfBooleanParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
