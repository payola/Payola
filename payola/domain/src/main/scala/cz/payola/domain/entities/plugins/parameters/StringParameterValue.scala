package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins.ParameterValue

class StringParameterValue(parameter: StringParameter, value: String)
    extends ParameterValue[String](parameter, value)
