package cz.payola.data.squeryl.entities.plugins

import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.plugins.ParameterValue]] to proper parameter value
  * in [[cz.payola.data.squeryl.entities.plugins.parameters]] package.
  *
  */
object ParameterValue
{
    def apply(pv: cz.payola.common.entities.plugins.ParameterValue[_])
        (implicit context: SquerylDataContextComponent): ParameterValue[_] = {
        pv match {
            case b: BooleanParameterValue => b
            case f: FloatParameterValue => f
            case i: IntParameterValue => i
            case s: StringParameterValue => s
            case b: cz.payola.domain.entities.plugins.parameters.BooleanParameterValue => BooleanParameterValue(b)
            case f: cz.payola.domain.entities.plugins.parameters.FloatParameterValue => FloatParameterValue(f)
            case i: cz.payola.domain.entities.plugins.parameters.IntParameterValue => IntParameterValue(i)
            case s: cz.payola.domain.entities.plugins.parameters.StringParameterValue => StringParameterValue(s)
        }
    }
}

trait ParameterValue[A] extends cz.payola.domain.entities.plugins.ParameterValue[A] with PersistableEntity
{
    val parameterId: String

    var pluginInstanceId: Option[String] = None

    var dataSourceId: Option[String] = None

    /**
      * @return Returns parameter this ParameterValue is associated to.
      */
    override def parameter: ParameterType
}
