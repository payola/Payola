package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class IntParameter(
    override val id: String,
    name: String,
    override val defaultValue: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameter(name, defaultValue)
    with Parameter[Int]
{

    private lazy val _instances = PayolaDB.valuesOfIntParameters.left(this)

    def parameterValues: Seq[IntParameterValue] = evaluateCollection(_instances)
}


