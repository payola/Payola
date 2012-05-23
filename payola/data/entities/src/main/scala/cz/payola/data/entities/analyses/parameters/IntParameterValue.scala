package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses._
import cz.payola.data.entities._

class IntParameterValue(
    override val id: String,
    param: IntParameter,
    override var value: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameterValue(param, value)
    with ParameterValue[Int]
{
    val parameterId: Option[String] = if (param == null) None else Some(param.id)

    private lazy val _parameterQuery = PayolaDB.valuesOfIntParameters.right(this)

    override def parameter: ParameterType = evaluateCollection(_parameterQuery)(0)
}
