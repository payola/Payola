package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.Parameter
import cz.payola.data.PayolaDB

object IntParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.IntParameter): IntParameter = {
        p match {
            case p: IntParameter => p
            case _ => new IntParameter(p.id, p.name, p.defaultValue)
        }
    }
}

class IntParameter(
    override val id: String,
    name: String,
    defaultVal: Int)
    extends cz.payola.domain.entities.plugins.parameters.IntParameter(name, defaultVal)
    with Parameter[Int]
{
    private lazy val _valuesQuery = PayolaDB.valuesOfIntParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[IntParameterValue] = evaluateCollection(_valuesQuery)

    def registerParameterValue(p: IntParameterValue) {
        associate(p, _valuesQuery)
    }
}


