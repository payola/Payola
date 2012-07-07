package cz.payola.data

import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.analyses.PluginInstanceBinding
import cz.payola.domain.RdfStorageComponent

trait DataContextComponent
{
    self: RdfStorageComponent =>

    val userRepository: Repository[User]

    val groupRepository: Repository[Group]

    val privilegeRepository: PrivilegeRepository[Privilege[_]]

    val analysisRepository: Repository[Analysis]

    val pluginRepository: Repository[Plugin]

    val pluginInstanceRepository: Repository[PluginInstance]

    val pluginInstanceBindingRepository: Repository[PluginInstanceBinding]

    val dataSourceRepository: Repository[DataSource]

    trait Repository[+A]
    {
        /**
          * Searches the repository for an entity with the specified ID.
          * @param id Id of an entity to search for.
          * @return The entity.
          */
        def getById(id: String): Option[A]

        /**
          * Removes an entity with the specified ID from the repository.
          * @param id Id of the entity to remove.
          * @return True if the entity was removed, false otherwise.
          */
        def removeById(id: String): Boolean

        /**
          * Returns all entities from the repository.
          * @param pagination Optionally specified pagination.
          * @return Returns all specified entities.
          */
        def getAll(pagination: Option[PaginationInfo] = None): Seq[A]

        /**
          * Persists the specified entity into the repository. If it already exists in the repository, then the entity
          * is updated.
          * @param entity The entity to persist.
          * @return The persisted entity.
          */
        def persist(entity: AnyRef): A
    }

    trait PrivilegeRepository[+A] extends Repository[A]
    {
        def getPrivilegeObjectIds(granteeId: String, privilegeClass: String, objectClass: String): Seq[String]
    }
}
