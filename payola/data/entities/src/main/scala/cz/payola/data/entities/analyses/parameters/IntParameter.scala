package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{Plugin, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}

class IntParameter(
    name: String,
    defaultValue: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameter(name, defaultValue)
    with PersistableEntity
    with Parameter[Int]
{

    private lazy val _instances = PayolaDB.valuesOfIntParameters.left(this)

    def instances: Seq[IntParameterValue] = evaluateCollection(_instances)
}


