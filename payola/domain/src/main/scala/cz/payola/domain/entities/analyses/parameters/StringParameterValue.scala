package cz.payola.domain.entities.analyses.parameters

import cz.payola.domain.entities.analyses.ParameterValue

class StringParameterValue(parameter: StringParameter, value: String)
    extends ParameterValue[String](parameter, value)
