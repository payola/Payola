package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.ParameterValue

class BooleanParameterValue(parameter: BooleanParameter, value: Boolean)
    extends ParameterValue[Boolean](parameter, value)
