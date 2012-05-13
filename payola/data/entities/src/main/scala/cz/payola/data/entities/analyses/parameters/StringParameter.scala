package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.{Plugin, Parameter}
import cz.payola.data.entities.{PayolaDB, PersistableEntity}
class StringParameter(
    name: String,
    defaultValue: String)
    extends cz.payola.domain.entities.analyses.parameters.StringParameter(name, defaultValue)
    with PersistableEntity
    with Parameter[String]
{

    private lazy val _instances = PayolaDB.valuesOfStringParameters.left(this)

    def instances: Seq[StringParameterValue] = evaluateCollection(_instances)
}


