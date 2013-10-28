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
            case e: cz.payola.common.entities.Prefix
                    => Some(new Prefix(e.id, e.name, e.prefix, e.url, e.owner.map(User(_))))
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.Prefix]] entities.
 * @param id ID of the prefix
 * @param name Name of the prefix
 * @param o Owner of the prefix
 * @param context Implicit context
 */
class Prefix (override val id: String, name: String, p: String, u: String, o: Option[User])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Prefix(name, p, u, o, id)
    with Entity with OptionallyOwnedEntity
{
    prefix = p
    url = u
}
