package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.Parameter
import cz.payola.data.PayolaDB

class FloatParameter(
    override val id: String,
    name: String,
    defaultVal: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameter(name, defaultVal)
    with Parameter[Float]
{
    private lazy val _instances = PayolaDB.valuesOfFloatParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[FloatParameterValue] = evaluateCollection(_instances)
}


