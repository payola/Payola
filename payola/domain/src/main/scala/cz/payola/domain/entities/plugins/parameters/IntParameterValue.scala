package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins.ParameterValue

class IntParameterValue(parameter: IntParameter, value: Int)
    extends ParameterValue[Int](parameter, value)
