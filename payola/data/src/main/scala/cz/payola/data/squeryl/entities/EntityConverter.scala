package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._
import cz.payola.data.DataException

trait EntityConverter[A <: Entity]
{
    def apply(entity: AnyRef)(implicit context: SquerylDataContextComponent): A = {
        convert(entity) match {
            case None => throw new DataException("Couldn't convert the entity to a data entity.")
            case e => e.get
        }
    }

    protected def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[A]
}
