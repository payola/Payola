package cz.payola.data.squeryl.entities.plugins.parameters

import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
 * This object converts [[cz.payola.domain.entities.plugins.parameters.FloatParameterValue]]
 * to [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]
 */
object FloatParameterValue
{
    def apply(p: cz.payola.domain.entities.plugins.parameters.FloatParameterValue)
        (implicit context: SquerylDataContextComponent): FloatParameterValue = {
        p match {
            case param: FloatParameterValue => param
            case _ => {
                val parameter = FloatParameter(
                    p.parameter.asInstanceOf[cz.payola.domain.entities.plugins.parameters.FloatParameter])
                val parameterValue = new FloatParameterValue(p.id, parameter, p.value)

                parameter.associateParameterValue(parameterValue)

                parameterValue
            }
        }
    }
}

class FloatParameterValue(
    override val id: String,
    param: FloatParameter,
    override var value: Float)(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.plugins.parameters.FloatParameterValue(param, value)
    with ParameterValue[Float]
{
    val parameterId: String = Option(param).map(_.id).getOrElse(null)
}
