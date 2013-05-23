package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean
import cz.payola.data.squeryl.entities._

/**
 * Provides repository to access persisted prefixes
 */
trait PrefixRepositoryComponent extends TableRepositoryComponent {
    self: SquerylDataContextComponent =>

    /**
     * A repository to access persisted analyses
     */
    lazy val prefixRepository = new PrefixDefaultTableRepository

    class PrefixDefaultTableRepository
        extends OptionallyOwnedEntityDefaultTableRepository[Prefix](schema.prefixes, Prefix)
        with PrefixRepository
        with NamedEntityTableRepository[Prefix]
    {
        def getAllAvailableToUser(userId: Option[String]): Seq[Prefix] = {
            selectWhere(p => p.ownerId === userId or p.ownerId === None)
        }
    }
}
