package cz.payola.model.components

import cz.payola.model.EntityModelComponent
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.Prefix

/**
 *
 */
trait PrefixModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PrivilegeModelComponent =>

    lazy val prefixModel = new EntityModel(prefixRepository)
    {
        /**
         * Gets all public prefixes available to user - default (unowned) and his own.
         * @param userId Id of a user to search prefixes for
         * @return Returns prefixes available to user
         */
        def getAllAvailableToUser(userId: Option[String]): Seq[Prefix] =
            prefixRepository.getAllAvailableToUser(userId)
    }
}
