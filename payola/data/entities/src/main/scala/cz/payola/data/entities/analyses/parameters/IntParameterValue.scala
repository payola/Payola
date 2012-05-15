package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.PersistableEntity

class IntParameterValue(
    parameter: cz.payola.domain.entities.analyses.parameters.IntParameter,
    value: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameterValue(parameter, value)
    with ParameterValue[Int]
{
}
