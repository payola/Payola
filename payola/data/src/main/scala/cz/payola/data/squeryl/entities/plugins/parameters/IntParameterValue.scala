package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This objects converts [[cz.payola.domain.entities.plugins.parameters.IntParameterValue]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]
 */
object IntParameterValue
{
    def apply(p: cz.payola.domain.entities.plugins.parameters.IntParameterValue)
        (implicit context: SquerylDataContextComponent): IntParameterValue = {
        p match {
            case param: IntParameterValue => param
            case _ => {
                val parameter = IntParameter(
                    p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.IntParameter])
                val parameterValue = new IntParameterValue(p.id, parameter, p.value)

                parameter.associateParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class IntParameterValue(
    override val id: String,
    param: IntParameter,
    override var value: Int)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.IntParameterValue(param, value)
    with ParameterValue[Int]
{
    val parameterId: String = Option(param).map(_.id).getOrElse(null)
}
