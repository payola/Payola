package cz.payola.data.entities.analyses

import cz.payola.data.entities._
import scala.collection.immutable

trait ParameterDbRepresentation[A] extends PersistableEntity
{
    var pluginId: Option[String] = None

    def instances: immutable.Seq[ParameterValue[A]]
}
