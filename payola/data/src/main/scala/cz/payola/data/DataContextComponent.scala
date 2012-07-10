package cz.payola.data

import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.analyses.PluginInstanceBinding
import cz.payola.domain.RdfStorageComponent
import cz.payola.data.squeryl.entities.PersistableEntity

/**
  * A component that provides access to a storage with persisted entities.
  */
trait DataContextComponent
{
    self: RdfStorageComponent =>

    val userRepository: UserRepository[User]

    val groupRepository: GroupRepository[Group]

    val privilegeRepository: PrivilegeRepository[Privilege[_]]

    val analysisRepository: AnalysisRepository[Analysis]

    val pluginRepository: PluginRepository[Plugin]

    val pluginInstanceRepository: Repository[PluginInstance]

    val pluginInstanceBindingRepository: Repository[PluginInstanceBinding]

    val dataSourceRepository: Repository[DataSource]

    lazy val repositoryRegistry = new RepositoryRegistry(Map(
        classOf[User] -> userRepository,
        classOf[Group] -> groupRepository,
        classOf[Privilege[_]] -> privilegeRepository,
        classOf[Analysis] -> analysisRepository,
        classOf[Plugin] -> pluginRepository,
        classOf[PluginInstance] -> pluginInstanceRepository,
        classOf[PluginInstanceBinding] -> pluginInstanceBindingRepository,
        classOf[DataSource] -> dataSourceRepository
    ))

    trait Repository[+A]
    {
        /**
          * Returns an entity with the specified ID.
          * @param id Id of an entity to search for.
          */
        def getById(id: String): Option[A] = getByIds(Seq(id)).headOption
        
        /**
          * Returns entities with the specified IDs.
          * @param ids List of IDs of entities to search for.
          */
        def getByIds(ids: Seq[String]): Seq[A]

        /**
          * Removes an entity with the specified ID from the repository.
          * @param id Id of the entity to remove.
          * @return True if the entity was removed, false otherwise.
          */
        def removeById(id: String): Boolean

        /**
          * Returns all entities from the repository.
          * @param pagination Optionally specified pagination.
          */
        def getAll(pagination: Option[PaginationInfo] = None): Seq[A]

        /**
          * Persists the specified entity into the repository. If it already exists in the repository, then the entity
          * is updated.
          * @param entity The entity to persist.
          * @return The persisted entity.
          */
        def persist(entity: AnyRef): A

        /**
          * @return Returns number of persisted entities
          */
        def getCount: Long
    }

    trait UserRepository[+A] extends Repository[A]
    {
        /**
          * Returns an user with the specified name.
          * @param name Name of an user to search for.
          */
        def getByName(name: String): Option[A]

        /**
          * Returns an user with the specified name and password.
          * @param name Name of the user to search for.
          * @param password Password of the user to search for.
          */
        def getByCredentials(name: String, password: String): Option[A]

        /**
          * Returns all users whose names contain the specified name part as a substring.
          * @param namePart Name part the users names must contain.
          * @param pagination Optionally specified pagination of the result.
          */
        def getAllWithNameLike(namePart: String, pagination: Option[PaginationInfo] = None): Seq[A]
    }

    trait GroupRepository[+A] extends Repository[A]
    {
        /**
          * Returns for all groups with the specified owner ID.
          * @param ownerId ID of the group owner.
          * @param pagination Optionally specified pagination of the result.
          */
        def getAllByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None) : Seq[A]
    }

    trait PrivilegeRepository[+A] extends Repository[A]
    {
        /**
          * Returns IDs of privileged objects, that are granted to the specified grantee via privileges of the specified
          * class.
          * @param granteeId ID of the privilege grantee.
          * @param privilegeClass Class of the privilege.
          * @param objectClass Class of the object.
          */
        def getPrivilegedObjectIds(granteeId: String, privilegeClass: Class[_], objectClass: Class[_]): Seq[String]
    }
    
    trait AnalysisRepository[+A] extends Repository[A]
    {
        /**
          * Returns top analyses in the repository.
          * @param pagination Optionally specified pagination of the result.
          */
        def getTop(pagination: Option[PaginationInfo] = Some(PaginationInfo(0, 10))): Seq[A]

        /**
          * Returns top analyses owned by the specified owner.
          * @param ownerId ID of the analysis owner.
          * @param pagination Optionally specified pagination of the result.
          */
        def getTopByOwner(ownerId: String, pagination: Option[PaginationInfo] = Some(PaginationInfo(0, 10))): Seq[A]

        /**
          * Returns public analyses owned by the specified owner.
          * @param ownerId ID of the analysis owner.
          * @param pagination Optionally specified pagination of the result.
          */
        def getPublicByOwner(ownerId: String, pagination: Option[PaginationInfo] = None): Seq[A]
    }

    trait PluginRepository[+A] extends Repository[A]
    {
        /**
          * Returns a plugin with the specified name.
          * @param name Name of the plugin to search for.
          */
        def getByName(name: String): Option[Plugin]
    }

    trait DataSourceRepository[+A] extends Repository[A]
    {
        /**
          * Returns all public data sources.
          * @param pagination Optionally specified pagination of the result.
          */
        def getPublic(pagination: Option[PaginationInfo] = None): Seq[A]
    }

    /**
      * A registry providing repositories by entity classes or entity class names.
      * @param repositoriesByClass The repositories to store in the registry indexed by classes whose instances the
      *                            repositories contain.
      */
    class RepositoryRegistry(repositoriesByClass: Map[Class[_], Repository[_]])
    {
        private val repositoriesByClassName = repositoriesByClass.map(r => getClassName(r._1) -> r._2)

        /**
          * Returns a repository by an entity class.
          * @param entityClass Class whose instances the repository contains.
          */
        def apply(entityClass: Class[_]): Repository[_] = {
            apply(getClassName(entityClass))
        }

        /**
          * Returns a repository by an entity class name.
          * @param entityClassName Name of the class whose instances the repository contains.
          */
        def apply(entityClassName: String): Repository[_] = {
            repositoriesByClassName.getOrElse(entityClassName, throw new DataException("A repository for class " +
                entityClassName + " doesn't exist."))
        }

        /**
          * Returns name of the class used for the purposes of the repository.
          * @param entityClass The class whose name to return.
          */
        def getClassName(entityClass: Class[_]): String = {
            val className = entityClass.getName
            className.drop(className.lastIndexOf(".") + 1)
        }
    }
}
