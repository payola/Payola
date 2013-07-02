package cz.payola.model.components

import cz.payola.model.EntityModelComponent
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.Prefix
import cz.payola.domain.Entity
import cz.payola.domain.entities.User

/**
 *
 */
trait PrefixModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>

    lazy val prefixModel = new EntityModel(prefixRepository)
    {
        override def persist(entity: Entity) {
            uniqueKeyCheckedPersist(entity, "prefix or url")
        }

        /**
         * Creates new prefix with specified values
         * @param prefix Prefix
         * @param url Url
         * @param owner Prefix owner
         * @return Returns persisted prefix
         */
        def create(prefix: String, url: String, owner: Option[User]): Prefix = {
            val p = new Prefix(prefix, prefix, url, owner)
            persist(p)
            p
        }

        /**
         * Gets all public prefixes available to user - default (unowned) and his own.
         * @param userId Id of a user to search prefixes for
         * @return Returns prefixes available to user
         */
        def getAllAvailableToUser(userId: Option[String]): Seq[Prefix] =
            prefixRepository.getAllAvailableToUser(userId)
    }
}
