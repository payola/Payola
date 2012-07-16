package cz.payola.data

import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.analyses.PluginInstanceBinding
import cz.payola.domain.entities.settings.OntologyCustomization

/**
  * A component that provides access to a storage with persisted entities.
  */
trait DataContextComponent
{
    val userRepository: UserRepository

    val groupRepository: GroupRepository

    val privilegeRepository: PrivilegeRepository

    val analysisRepository: AnalysisRepository

    val pluginRepository: PluginRepository

    val dataSourceRepository: DataSourceRepository

    val ontologyCustomizationRepository: OntologyCustomizationRepository

    lazy val repositoryRegistry = new RepositoryRegistry(Map(
        classOf[User] -> userRepository,
        classOf[Group] -> groupRepository,
        classOf[Privilege[_]] -> privilegeRepository,
        classOf[Analysis] -> analysisRepository,
        classOf[Plugin] -> pluginRepository,
        classOf[DataSource] -> dataSourceRepository,
        classOf[OntologyCustomization] -> ontologyCustomizationRepository
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

    /**
      * A repository that contains named entities.
      * @tparam A Type of the entities in the repository.
      */
    trait NamedEntityRepository[+A <: NamedEntity] extends Repository[A]
    {
        /**
          * Returns an entity with the specified name.
          * @param name Name of the entity to search for.
          */
        def getByName(name: String): Option[A]
    }

    /**
      * A repository that contains optionally owned entities.
      * @tparam A Type of the entities in the repository.
      */
    trait OptionallyOwnedEntityRepository[+A <: OptionallyOwnedEntity] extends Repository[A]
    {
        /**
          * Returns all entities with the specified owner.
          * @param ownerId Owner id of the entities to search for.
          */
        def getAllByOwnerId(ownerId: Option[String]): Seq[A]
    }

    /**
      * A repository that contains shareable entities.
      * @tparam A Type of the entities in the repository.
      */
    trait ShareableEntityRepository[+A <: ShareableEntity with OptionallyOwnedEntity]
        extends OptionallyOwnedEntityRepository[A]
    {
        /**
          * Returns all public entities.
          */
        def getAllPublic: Seq[A]

        /**
          * Returns all public entities with the specified owner.
          * @param ownerId Owner id of the entities to search for.
          */
        def getAllPublicByOwnerId(ownerId: Option[String]): Seq[A] = getAllByOwnerId(ownerId).filter(_.isPublic)
    }

    trait UserRepository
        extends Repository[User]
        with NamedEntityRepository[User]
    {
        /**
          * Returns an user with the specified name and password.
          * @param name Name of the user to search for.
          * @param password Password of the user to search for.
          */
        def getByCredentials(name: String, password: String): Option[User]

        /**
          * Returns all users whose names contain the specified name part as a substring.
          * @param namePart Name part the users names must contain.
          * @param pagination Optionally specified pagination of the result.
          */
        def getAllWithNameLike(namePart: String, pagination: Option[PaginationInfo] = None): Seq[User]
    }

    trait GroupRepository extends Repository[Group]
    {
        /**
          * Returns for all groups with the specified owner ID.
          * @param ownerId ID of the group owner.
          * @param pagination Optionally specified pagination of the result.
          */
        def getAllByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None) : Seq[Group]
    }

    trait PrivilegeRepository extends Repository[Privilege[_ <: cz.payola.domain.Entity]]
    {
        /**
          * Returns all privileges of the specified type granted to the specified grantees.
          * @param granteeIds The entities whose privileges should be returned.
          * @param privilegeClass Type of the privilege.
          */
        def getAllGrantedTo(granteeIds: Seq[String], privilegeClass: Class[_]): Seq[Privilege[_ <: cz.payola.domain.Entity]]

        /**
          * Returns IDs of privileged objects, that are granted to the specified grantee via privileges of the specified
          * class.
          * @param granteeId ID of the privilege grantee.
          */
        def getByGrantee(granteeId: String): Seq[Privilege[_ <: cz.payola.domain.Entity]]
    }
    
    trait AnalysisRepository
        extends Repository[Analysis]
        with NamedEntityRepository[Analysis]
        with OptionallyOwnedEntityRepository[Analysis]
        with ShareableEntityRepository[Analysis]
    {
        /**
          * Persists specified PluginInstance of Analysis
          * @param pluginInstance PluginInstance to persist
          */
        def persistPluginInstance(pluginInstance: PluginInstance)

        /**
          * Persists given ParameterValue
          * @param parameterValue ParameterValue to persist
          */
        def persistParameterValue(parameterValue: ParameterValue[_])
    }

    trait OntologyCustomizationRepository
        extends Repository[OntologyCustomization]
        with NamedEntityRepository[OntologyCustomization]
        with OptionallyOwnedEntityRepository[OntologyCustomization]
        with ShareableEntityRepository[OntologyCustomization]

    trait PluginRepository
        extends Repository[Plugin]
        with NamedEntityRepository[Plugin]
        with OptionallyOwnedEntityRepository[Plugin]
        with ShareableEntityRepository[Plugin]

    trait DataSourceRepository
        extends Repository[DataSource]
        with NamedEntityRepository[DataSource]
        with OptionallyOwnedEntityRepository[DataSource]
        with ShareableEntityRepository[DataSource]
    {
        /**
          * Persists specified PluginInstance of Analysis
          * @param pluginInstance PluginInstance to persist
          */
        def persistPluginInstance(pluginInstance: PluginInstance)

        /**
          * Persists ParameterValue of PluginInstance of Analysis
          * @param parameterValue ParameterValue to persist
          */
        def persistParameterValue(parameterValue: ParameterValue[_])
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
