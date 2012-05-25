package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.ParameterValue

class IntParameterValue(parameter: IntParameter, value: Int)
    extends ParameterValue[Int](parameter, value)
