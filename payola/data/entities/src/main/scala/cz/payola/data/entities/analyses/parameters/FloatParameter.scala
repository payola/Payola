package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{Plugin, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}

class FloatParameter(
    name: String,
    defaultValue: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameter(name, defaultValue)
    with PersistableEntity
    with Parameter[Float]
{

    private lazy val _instances = PayolaDB.valuesOfFloatParameters.left(this)

    def instances: Seq[FloatParameterValue] = evaluateCollection(_instances)
}


