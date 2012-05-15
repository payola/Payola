package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, ParameterDbRepresentation}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class BooleanParameterDbRepresentation(
    name: String,
    defaultValue: Boolean)
    extends ParameterDbRepresentation[Boolean]
{
    private lazy val _instances = PayolaDB.valuesOfBooleanParameters.left(this)

    def instances: Seq[BooleanParameterValue] = evaluateCollection(_instances)
}


