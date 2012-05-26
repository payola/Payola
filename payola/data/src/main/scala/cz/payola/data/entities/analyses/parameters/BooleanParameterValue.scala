package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.ParameterValue
import cz.payola.data.PayolaDB

object BooleanParameterValue {

    def apply(p: cz.payola.domain.entities.analyses.parameters.BooleanParameterValue): BooleanParameterValue = {
        val parameter = BooleanParameter(p.parameter.asInstanceOf[cz.payola.domain.entities.analyses.parameters.BooleanParameter])
        new BooleanParameterValue(p.id, parameter, p.value)
    }
}

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
