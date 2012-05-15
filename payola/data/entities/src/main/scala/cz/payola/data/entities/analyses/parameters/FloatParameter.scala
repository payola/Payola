package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{Plugin, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
import cz.payola.domain.entities.analyses.ParameterValue

class FloatParameter(
    name: String,
    defaultValue: Float)
    extends cz.payola.domain.entities.analyses.parameters.FloatParameter(name, defaultValue)
    with PersistableEntity
    with Parameter[Float]
{
    private lazy val _instances = PayolaDB.valuesOfFloatParameters.left(this)

    def instances: Seq[FloatParameterValue] = evaluateCollection(_instances)

    override def createValue(value: Float) : ParameterValue[Float] = {
        new FloatParameterValue(this, value)
    }
}


