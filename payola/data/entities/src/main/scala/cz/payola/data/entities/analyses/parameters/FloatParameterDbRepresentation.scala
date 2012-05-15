package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, ParameterDbRepresentation}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class FloatParameterDbRepresentation(
    name: String,
    defaultValue: Float)
    extends ParameterDbRepresentation[Float]
{
    private lazy val _instances = PayolaDB.valuesOfFloatParameters.left(this)

    def instances: Seq[FloatParameterValue] = evaluateCollection(_instances)
}


