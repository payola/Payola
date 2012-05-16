package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class BooleanParameter(
    override val id: String,
    name: String,
    override val defaultValue: Boolean)
    extends cz.payola.domain.entities.analyses.parameters.BooleanParameter(name, defaultValue)
    with Parameter[Boolean]
{
    private lazy val _valuesQuery = PayolaDB.valuesOfBooleanParameters.left(this)

    val _value: Boolean = defaultValue

    def parameterValues: Seq[BooleanParameterValue] = evaluateCollection(_valuesQuery)
}


