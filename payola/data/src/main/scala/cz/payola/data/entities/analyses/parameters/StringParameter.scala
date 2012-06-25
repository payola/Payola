package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.Parameter
import cz.payola.data.PayolaDB

object StringParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.StringParameter): StringParameter = {
        p match {
            case param: StringParameter => param
            case _ => new StringParameter(p.id, p.name, p.defaultValue)
        }
    }
}

class StringParameter(
    override val id: String,
    name: String,
    defaultVal: String)
    extends cz.payola.domain.entities.plugins.parameters.StringParameter(name, defaultVal)
    with Parameter[String]
{
    private lazy val _valuesQuery = PayolaDB.valuesOfStringParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[StringParameterValue] = evaluateCollection(_valuesQuery)

    def registerParameterValue(p: StringParameterValue) {
        associate(p, _valuesQuery)
    }
}


