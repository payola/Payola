package cz.payola.data.entities.plugins.parameters

import cz.payola.data.entities.plugins.Parameter
import cz.payola.data.PayolaDB

/**
  * This object converts [[cz.payola.common.entities.plugins.parameters.BooleanParameter]]
  * to [[cz.payola.common.entities.plugins.parameters.BooleanParameter]]
  */
object BooleanParameter {

    def apply(p: cz.payola.common.entities.plugins.parameters.BooleanParameter): BooleanParameter = {
        p match {
            case p: BooleanParameter => p
            case _ => new BooleanParameter(p.id, p.name, p.defaultValue)
        }
    }
}

class BooleanParameter(
    override val id: String,
    name: String,
    defaultVal: Boolean)
    extends cz.payola.domain.entities.plugins.parameters.BooleanParameter(name, defaultVal)
    with Parameter[Boolean]
{
    private lazy val _valuesQuery = PayolaDB.valuesOfBooleanParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[BooleanParameterValue] = evaluateCollection(_valuesQuery)

    /**
      * Associates specified [[cz.payola.data.entities.plugins.parameters.BooleanParameter]].
      *
      * @param p - [[cz.payola.data.entities.plugins.parameters.BooleanParameter]] to associate
      */
    def associateParameterValue(p: BooleanParameterValue) {
        associate(p, _valuesQuery)
    }
}


