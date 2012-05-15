package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, ParameterDbRepresentation}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class IntParameterDbRepresentation(
    name: String,
    defaultValue: Int)
    extends ParameterDbRepresentation[Int]
{
    private lazy val _instances = PayolaDB.valuesOfIntParameters.left(this)

    def instances: Seq[IntParameterValue] = evaluateCollection(_instances)
}


