package cz.payola.data.entities.analyses.parameters

import cz.payola.data.entities.analyses.Parameter
import cz.payola.data.entities.PayolaDB

object IntParameter {

    def apply(p: cz.payola.common.entities.analyses.parameters.IntParameter): IntParameter = {
        new IntParameter(p.id, p.name, p.defaultValue)
    }
}

class IntParameter(
    override val id: String,
    name: String,
    defaultVal: Int)
    extends cz.payola.domain.entities.analyses.parameters.IntParameter(name, defaultVal)
    with Parameter[Int]
{
    private lazy val _instances = PayolaDB.valuesOfIntParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[IntParameterValue] = evaluateCollection(_instances)
}


