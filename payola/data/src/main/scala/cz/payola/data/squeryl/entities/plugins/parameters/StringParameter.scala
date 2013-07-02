package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This object converts [[cz.payola.common.entities.plugins.parameters.StringParameter]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]
 */
object StringParameter
{
    def apply(p: cz.payola.common.entities.plugins.parameters.StringParameter)
        (implicit context: SquerylDataContextComponent): StringParameter = {
        p match {
            case param: StringParameter => param
            case _ => new StringParameter(
                p.id, p.name, p.defaultValue, p.isMultiline, p.isPattern, p.isPassword, p.canContainUrl, p.ordering)
        }
    }
}

class StringParameter(
    override val id: String,
    name: String,
    defaultVal: String,
    isMultiline: Boolean, isPattern: Boolean, isPassword: Boolean, canContainUrl: Boolean, ordering: Option[Int])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.StringParameter(
        name, defaultVal, isMultiline, isPattern, isPassword, canContainUrl, ordering)
    with Parameter[String]
{
    private lazy val _valuesQuery = context.schema.valuesOfStringParameters.left(this)

    // Get, store and set default value of parameter to Database
    val _defaultValueDb = defaultVal

    override def defaultValue = _defaultValueDb

    def parameterValues: Seq[StringParameterValue] = wrapInTransaction {
        _valuesQuery.toList
    }

    /**
     * Associates specified [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]].
     *
     * @param p - [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]] to associate
     */
    def associateParameterValue(p: StringParameterValue) {
        context.schema.associate(p, _valuesQuery)
    }
}


