package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.PersistableEntity

class IntParameterValue(
    parameter: IntParameter,
    value: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameterValue(parameter, value)
    with PersistableEntity
    with ParameterValue[Int]
{
}
