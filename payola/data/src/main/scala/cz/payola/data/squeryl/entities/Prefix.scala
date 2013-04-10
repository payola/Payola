package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.data.squeryl.Entity

/**
 * This object converts [[cz.payola.common.entities.Prefix]] to [[cz.payola.common.entities.Prefix]].
 */
object Prefix extends EntityConverter[Prefix]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Prefix] = {
        entity match {
            case e: Prefix => Some(e)
            case e: cz.payola.common.entities.Prefix => Some(new Prefix(e.id, e.name, e.prefix, e.url))
            case _ => None
        }
    }
}

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
class Prefix (override val id: String, name: String, p: String, u: String)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Prefix(name, p, u, id)
    with Entity with OptionallyOwnedEntity with ShareableEntity
{
    prefix = p
    url = u
}
