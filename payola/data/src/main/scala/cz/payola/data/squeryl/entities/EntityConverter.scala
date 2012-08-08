package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl._
import cz.payola.data.DataException

/**
 * Converter that converts some [[cz.payola.common.Entity]] and to corresponding entity in data layer.
 * @tparam A Type of [[cz.payola.data.squeryl.Entity]] that is a result of the conversion
 */
trait EntityConverter[A <: Entity]
{
    /**
     * Starts the entity conversion. When conversion fails a [[cz.payola.data.DataException]] is thrown.
     * @param entity Entity to be converted
     * @param context Implicit context for converted entity
     * @return Returns converted entity or throws DataException
     */
    def apply(entity: AnyRef)(implicit context: SquerylDataContextComponent): A = {
        convert(entity) match {
            case None => throw new DataException("Couldn't convert the entity to a data entity.")
            case e => e.get
        }
    }

    /**
     * Tries to convert the entity.
     * @param entity Entity to be converted
     * @param context Implicit context
     * @return Returns converted entity as an Option, or None when conversion was unsuccessful
     */
    protected def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[A]
}
