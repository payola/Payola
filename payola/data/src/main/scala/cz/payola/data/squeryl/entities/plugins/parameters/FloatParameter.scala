package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This objects converts [[cz.payola.common.entities.plugins.parameters.FloatParameter]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]
 */
object FloatParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.FloatParameter)
        (implicit context: SquerylDataContextComponent): FloatParameter = {
        p match {
            case p: FloatParameter => p
            case _ => new FloatParameter(p.id, p.name, p.defaultValue, p.ordering)
        }
    }
}

class FloatParameter(
    override val id: String,
    name: String,
    defaultVal: Float, ordering: Option[Int])(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.FloatParameter(name, defaultVal, ordering)
    with Parameter[Float]
{
    private lazy val _valuesQuery = context.schema.valuesOfFloatParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[FloatParameterValue] = wrapInTransaction {
        _valuesQuery.toList
    }

    /**
     * Associates specified [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]].
     *
     * @param p - [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]] to associate
     */
    def associateParameterValue(p: FloatParameterValue) {
        context.schema.associate(p, _valuesQuery)
    }
}


