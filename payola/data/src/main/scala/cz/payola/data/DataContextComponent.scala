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
        /**
          * Loads [[cz.payola.common.entities.Privilege]] by privileged object class and privileged grantee
          *
          * @param granteeId - id of [[cz.payola.common.entities.PrivilegableEntity]] that has privilege
          * @param privilegeClass - stripped class name of [[cz.payola.common.entities.Privilege]] assigned to grantee Entity
          * @param objectClass - stripped class name of objects that are subjects of the Privilege
          *
          * @see [[cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation.stripClassName()]] method          *
          *
          * @return Returns list of Privileges
          */
        def getPrivilegedObjectIds(granteeId: String, privilegeClass: String, objectClass: String): Seq[String]

        /**
          *
          * @return Returns number of privileges in database
          */
        def getPrivilegesCount(): Int

    }
    
    trait AnalysisRepositoryComponent[+A] extends Repository[A]
    {
        /**
          * Returns TOP analyses from all users.
          *
          * @param pagination - Optionally specified pagination of analyses
          * @return Returns collection of TOP analyses
          */
        def getTopAnalyses(pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[A]

        /**
          * Returns TOP analyses from specified user.
          *
          * @param ownerId - id of analyses owner
          * @param pagination - Optionally specified pagination of analyses
          * @return Returns collection of TOP analyses
          */
        def getTopAnalysesByUser(ownerId: String, pagination: Option[PaginationInfo] = Some(new PaginationInfo(0, 10))): collection.Seq[A]

        /**
          * Returns public analyses of specified owner
          *
          * @param ownerId - id of analyses owner
          * @param pagination - Optionally specified pagination
          * @return Returns collection of analyses
          */
        def getPublicAnalysesByOwner(ownerId: String, pagination: Option[PaginationInfo] = None)
    }

    trait DataSourceRepositoryComponent[+A] extends Repository[A]
    {
        /**
          * Returns collection of public [[cz.payola.data.squeryl.entities.plugins.DataSource]].
          * Result may be paginated.
          *
          * @param pagination - Optionally specified pagination
          * @return Returns collection of public [[cz.payola.data.squeryl.entities.plugins.DataSource]]
          */
        def getPublicDataSources(pagination: Option[PaginationInfo] = None): Seq[A]

    }

    trait GroupRepositoryComponent[+A] extends Repository[A]
    {
        /**
          * Returns [[cz.payola.data.squeryl.entities.Group]]s owned by specified owner. Result may be paginated
          *
          * @param ownerId - id of groups owner
          * @param pagination - Optionally specified pagination
          * @return Returns collection of [[cz.payola.data.squeryl.entities.Group]]s
          */
        def getByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None) : Seq[A]
    }

    trait UserRepositoryComponent[A] extends Repository[A]
    {
        /**
          * Searches for all [[cz.payola.data.squeryl.entities.User]]s, whose username CONTAINS specified name.
          * Result may be paginated.
          *
          * @param name - username (or just its part) that must appear in users username
          * @param pagination - Optionally specified pagination of result
          * @return Return collection of [[cz.payola.data.squeryl.entities.User]]s
          */
        def findByUsername(name: String, pagination: Option[PaginationInfo] = None): Seq[A]

        /**
          * Finds [[cz.payola.data.squeryl.entities.User]] with specified username.
          *
          * @param username - username of user to find
          * @return Returns Option([[cz.payola.data.squeryl.entities.User]]) if found, None otherwise
          */
        def getUserByUsername(username: String): Option[A]

        /**
          * Finds [[cz.payola.data.squeryl.entities.User]] with specified username and password.
          *
          * @param username - username of user
          * @param password - encrypted password of user
          * @return Returns Option([[cz.payola.data.squeryl.entities.User]]) if found, None otherwise
          */
        def getUserByCredentials(username: String, password: String): Option[A]

    }

    trait PluginRepositoryComponent[A] extends Repository[A]
    {
        /**
          * Returns [[cz.payola.domain.entities.Plugin]] by its name.
          *
          * @param pluginName - name of a plugin to search
          * @return Return Some([[cz.payola.domain.entities.Plugin]]) if found, None otherwise
          */
        def getByName(pluginName: String): Option[A]
    }
    
    trait PluginInstanceBindingRepositoryComponent[A] extends Repository[A] {}

    trait PluginInstanceRepositoryComponent[A] extends Repository[A] {}

}
