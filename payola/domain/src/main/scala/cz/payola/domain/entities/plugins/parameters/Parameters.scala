package cz.payola.domain.entities.plugins.parameters

import cz.payola.domain.entities.plugins._

class BooleanParameter(name: String, defaultValue: Boolean, ordering: Option[Int] = None)
    extends Parameter[Boolean](name, defaultValue, ordering) with cz.payola.common.entities.plugins.parameters.BooleanParameter
{
    def createValue(value: Boolean): ParameterValue[Boolean] = {
        new BooleanParameterValue(this, value)
    }
}

class FloatParameter(name: String, defaultValue: Float, ordering: Option[Int] = None)
    extends Parameter[Float](name, defaultValue, ordering) with cz.payola.common.entities.plugins.parameters.FloatParameter
{
    def createValue(value: Float): ParameterValue[Float] = {
        new FloatParameterValue(this, value)
    }
}

class IntParameter(name: String, defaultValue: Int, ordering: Option[Int] = None)
    extends Parameter[Int](name, defaultValue, ordering) with cz.payola.common.entities.plugins.parameters.IntParameter
{
    def createValue(value: Int): ParameterValue[Int] = {
        new IntParameterValue(this, value)
    }
}

class StringParameter(name: String, defaultValue: String,
    protected val _isMultiline: Boolean = false,
    protected val _isPattern: Boolean = false,      // added in order to handle pattern selection [Jjiri Helmich]
    protected val _isPassword: Boolean = false,     // added in order to handle passwords [Jjiri Helmich]
    protected val _canContainUrl: Boolean = false,
    ordering: Option[Int] = None)
    extends Parameter[String](name, defaultValue, ordering) with cz.payola.common.entities.plugins.parameters.StringParameter
{
    def createValue(value: String): ParameterValue[String] = {
        new StringParameterValue(this, value)
    }
}
