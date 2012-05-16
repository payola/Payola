package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class FloatParameter(
    override val id: String,
    name: String,
    override val defaultValue: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameter(name, defaultValue)
    with Parameter[Float]
{
    private lazy val _instances = PayolaDB.valuesOfFloatParameters.left(this)

    def parameterValues: Seq[FloatParameterValue] = evaluateCollection(_instances)
}


