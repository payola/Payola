package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{PluginDbRepresentation, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class IntParameter(
    override val id: String,
    name: String,
    defaultVal: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameter(name, defaultVal)
    with Parameter[Int]
{
    private lazy val _instances = PayolaDB.valuesOfIntParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[IntParameterValue] = evaluateCollection(_instances)
}


