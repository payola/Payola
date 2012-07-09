package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins.ParameterValue

class BooleanParameterValue(parameter: BooleanParameter, value: Boolean)
    extends ParameterValue[Boolean](parameter, value)

class FloatParameterValue(parameter: FloatParameter, value: Float)
    extends ParameterValue[Float](parameter, value)

class IntParameterValue(parameter: IntParameter, value: Int)
    extends ParameterValue[Int](parameter, value)

class StringParameterValue(parameter: StringParameter, value: String)
    extends ParameterValue[String](parameter, value)
