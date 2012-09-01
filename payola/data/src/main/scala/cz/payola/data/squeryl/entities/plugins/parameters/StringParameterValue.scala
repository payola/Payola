package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins.ParameterValue
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This obejct converts [[cz.payola.domain.entities.plugins.parameters.StringParameterValue]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValue]]
 */
object StringParameterValue
{
    def apply(p: cz.payola.domain.entities.plugins.parameters.StringParameterValue)
        (implicit context: SquerylDataContextComponent): StringParameterValue = {
        p match {
            case param: StringParameterValue => param
            case _ => {
                val parameter = StringParameter(
                    p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter])
                val parameterValue = new StringParameterValue(p.id, parameter, p.value)

                parameter.associateParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class StringParameterValue(
    override val id: String,
    param: StringParameter,
    override var value: String)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.StringParameterValue(param, value)
    with ParameterValue[String]
{
    val parameterId: String = Option(param).map(_.id).getOrElse(null)
}
