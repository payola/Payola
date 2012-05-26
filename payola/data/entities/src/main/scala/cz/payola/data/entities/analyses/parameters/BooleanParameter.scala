package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.Parameter
import cz.payola.data.entities.PayolaDB

object BooleanParameter {

    def apply(p: cz.payola.domain.entities.analyses.parameters.BooleanParameter): BooleanParameter = {
        new BooleanParameter(p.id, p.name, p.defaultValue)
    }
}

class BooleanParameter(
    override val id: String,
    name: String,
    defaultVal: Boolean)
    extends cz.payola.domain.entities.analyses.parameters.BooleanParameter(name, defaultVal)
    with Parameter[Boolean]
{
    private lazy val _valuesQuery = PayolaDB.valuesOfBooleanParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[BooleanParameterValue] = evaluateCollection(_valuesQuery)
}


