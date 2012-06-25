package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins.ParameterValue

class BooleanParameterValue(parameter: BooleanParameter, value: Boolean)
    extends ParameterValue[Boolean](parameter, value)
