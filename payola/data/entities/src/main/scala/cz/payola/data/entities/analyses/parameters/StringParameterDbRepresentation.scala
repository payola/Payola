package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, ParameterDbRepresentation}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class StringParameterDbRepresentation(
    name: String,
    defaultValue: String)
    extends ParameterDbRepresentation[String]
{
    private lazy val _instances = PayolaDB.valuesOfStringParameters.left(this)

    def instances: Seq[StringParameterValue] = evaluateCollection(_instances)
}


