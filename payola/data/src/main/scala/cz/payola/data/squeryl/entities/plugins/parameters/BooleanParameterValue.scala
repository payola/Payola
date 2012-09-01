package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This object converts [[cz.payola.domain.entities.plugins.parameters.BooleanParameterValue]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValue]]
 */
object BooleanParameterValue
{
    def apply(p: cz.payola.domain.entities.plugins.parameters.BooleanParameterValue)
        (implicit context: SquerylDataContextComponent): BooleanParameterValue = {
        p match {
            case param: BooleanParameterValue => param
            case _ => {
                val parameter = BooleanParameter(
                    p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.BooleanParameter])
                val parameterValue = new BooleanParameterValue(p.id, parameter, p.value)

                parameter.associateParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class BooleanParameterValue(
    override val id: String,
    param: BooleanParameter,
    override var value: Boolean)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.BooleanParameterValue(param, value)
    with ParameterValue[Boolean]
{
    val parameterId: String = Option(param).map(_.id).getOrElse(null)
}
