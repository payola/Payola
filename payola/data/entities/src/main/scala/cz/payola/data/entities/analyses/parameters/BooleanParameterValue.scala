package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.PersistableEntity

class BooleanParameterValue(
    parameter: cz.payola.domain.entities.analyses.parameters.BooleanParameter,
    value: Boolean)
    extends cz.payola.domain.entities.analyses.parameters.BooleanParameterValue(parameter, value)
    with ParameterValue[Boolean]
{
}
