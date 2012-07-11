package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.plugins.parameters.IntParameter]]
  * to [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]
  */
object IntParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.IntParameter)
        (implicit context: SquerylDataContextComponent): IntParameter = {
        p match {
            case p: IntParameter => p
            case _ => new IntParameter(p.id, p.name, p.defaultValue)
        }
    }
}

class IntParameter(
    override val id: String,
    name: String,
    defaultVal: Int)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.IntParameter(name, defaultVal)
    with Parameter[Int]
{
    private lazy val _valuesQuery = context.schema.valuesOfIntParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[IntParameterValue] = wrapInTransaction { _valuesQuery.toList }

    /**
      * Associates specified [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]].
      *
      * @param p - [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]] to associate
      */
    def associateParameterValue(p: IntParameterValue) {
        associate(p, _valuesQuery)
    }
}


