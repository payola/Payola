package cz.payola.data.squeryl.entities.plugins

import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl._

/**
  * This object converts [[cz.payola.common.entities.plugins.ParameterValue]] to proper parameter value
  * in [[cz.payola.data.squeryl.entities.plugins.parameters]] package.
  *
  */
object ParameterValue extends EntityConverter[ParameterValue[_]]
{
    def convert(entity: AnyRef)
        (implicit context: SquerylDataContextComponent): Option[ParameterValue[_]] = {
        entity match {
            case b: BooleanParameterValue => Some(b)
            case f: FloatParameterValue => Some(f)
            case i: IntParameterValue => Some(i)
            case s: StringParameterValue => Some(s)
            case b: cz.payola.domain.entities.plugins.parameters.BooleanParameterValue => Some(BooleanParameterValue(b))
            case f: cz.payola.domain.entities.plugins.parameters.FloatParameterValue => Some(FloatParameterValue(f))
            case i: cz.payola.domain.entities.plugins.parameters.IntParameterValue => Some(IntParameterValue(i))
            case s: cz.payola.domain.entities.plugins.parameters.StringParameterValue => Some(StringParameterValue(s))
            case _ => None
        }
    }
}

trait ParameterValue[A] extends cz.payola.domain.entities.plugins.ParameterValue[A] with Entity
{
    val parameterId: String

    var pluginInstanceId: Option[String] = None

    var dataSourceId: Option[String] = None

    def parameter_=(value: Parameter[_]) { _parameter = value.asInstanceOf[ParameterType] }
}
