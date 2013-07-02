package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This object converts [[cz.payola.common.entities.plugins.parameters.BooleanParameter]]
 * to [[cz.payola.common.entities.plugins.parameters.BooleanParameter]]
 */
object BooleanParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.BooleanParameter)
        (implicit context: SquerylDataContextComponent): BooleanParameter = {
        p match {
            case p: BooleanParameter => p
            case _ => new BooleanParameter(p.id, p.name, p.defaultValue, p.ordering)
        }
    }
}

class BooleanParameter(
    override val id: String,
    name: String,
    defaultVal: Boolean, ordering: Option[Int])(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.BooleanParameter(name, defaultVal, ordering)
    with Parameter[Boolean]
{
    private lazy val _valuesQuery = context.schema.valuesOfBooleanParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[BooleanParameterValue] = wrapInTransaction {
        _valuesQuery.toList
    }

    /**
     * Associates specified [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]].
     *
     * @param p - [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]] to associate
     */
    def associateParameterValue(p: BooleanParameterValue) {
        context.schema.associate(p, _valuesQuery)
    }
}


