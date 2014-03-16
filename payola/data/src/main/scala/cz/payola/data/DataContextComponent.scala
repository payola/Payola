package cz.payola.data

import cz.payola.common._
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.settings.Customization
import cz.payola.domain.entities.Prefix

/**
 * A component that provides access to a storage with persisted entities.
 */
trait DataContextComponent
{
    /**
     * A repository to access persisted users
     */
    val userRepository: UserRepository

    /**
     * A repository to access persisted groups
     */
    val groupRepository: GroupRepository

    /**
     * A repository to access persisted privileges
     */
    val privilegeRepository: PrivilegeRepository

    /**
     * A repository to access persisted analyses
     */
    val analysisRepository: AnalysisRepository

    /**
     * A repository to access persisted plugins
     */
    val pluginRepository: PluginRepository

    /**
     * A repository to access persisted data sources
     */
    val dataSourceRepository: DataSourceRepository

    /**
     * A repository to access persisted ontology customizations
     */
    val customizationRepository: CustomizationRepository

    /**
     * A repository to access persisted prefixes
     */
    val prefixRepository: PrefixRepository

    /**
     * A repository to access stored analyses results
     */
    val analysisResultRepository: AnalysisResultRepository

    /**
     * A repository to access persisted analyses results embedding data
     */
    val embeddingDescriptionRepository: EmbeddingDescriptionRepository

    /**
     * A registry that provides repositories by class name of persisted entity
     */
    lazy val repositoryRegistry = new RepositoryRegistry(Map(
        classOf[User] -> userRepository,
        classOf[Group] -> groupRepository,
        classOf[Privilege[_]] -> privilegeRepository,
        classOf[Analysis] -> analysisRepository,
        classOf[Plugin] -> pluginRepository,
        classOf[DataSource] -> dataSourceRepository,
        classOf[Customization] -> customizationRepository,
        classOf[Prefix] -> prefixRepository,
        classOf[EmbeddingDescription] -> embeddingDescriptionRepository,
        classOf[AnalysisResult] -> analysisResultRepository
    ))

    /**
     * Defines all operations common to all entity repositories
     * @tparam A Type of entities in the repository
     */
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
     * Defines operations common to all repositories accessing entities that extend
     * [[cz.payola.domain.entities.NamedEntity]].
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
     * Defines operations common to all repositories accessing entities that extend
     * [[cz.payola.domain.entities.OptionallyOwnedEntity]].
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
     * Defines operations common to all repositories accessing entities that extend
     * [[cz.payola.common.entities.ShareableEntity]].
     * @tparam A Type of the entities in the repository.
     */
    trait ShareableEntityRepository[+A <: ShareableEntity with OptionallyOwnedEntity with NamedEntity]
        extends OptionallyOwnedEntityRepository[A] with NamedEntityRepository[A]
    {
        /**
         * Returns all public entities.
         */
        def getAllPublic(forListing: Boolean = false): Seq[A]

        /**
         * Returns all public entities with the specified owner.
         * @param ownerId Owner id of the entities to search for.
         */
        def getAllPublicByOwnerId(ownerId: Option[String]): Seq[A] = getAllByOwnerId(ownerId).filter(_.isPublic)
    }

    /**
     * Defines operations of repository accessing users
     */
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
         * Returns an user with the specified email.
         * @param email Email of the user to search for.
         */
        def getByEmail(email: String): Option[User]

        /**
         * Returns all users whose names contain the specified name part as a substring.
         * @param namePart Name part the users names must contain.
         * @param pagination Optionally specified pagination of the result.
         */
        def getAllWithNameLike(namePart: String, pagination: Option[PaginationInfo] = None): Seq[User]
    }

    /**
     * Defines operations of repository accessing groups
     */
    trait GroupRepository extends Repository[Group]
    {
        /**
         * Returns for all groups with the specified owner ID.
         * @param ownerId ID of the group owner.
         * @param pagination Optionally specified pagination of the result.
         */
        def getAllByOwnerId(ownerId: String, pagination: Option[PaginationInfo] = None): Seq[Group]
    }

    /**
     * Defines operations of repository accessing privileges
     */
    trait PrivilegeRepository extends Repository[Privilege[_ <: Entity]]
    {
        /**
         * Returns all privileges of the specified type granted to the specified grantees.
         * @param granteeIds The entities whose privileges should be returned.
         * @param privilegeClass Type of the privilege.
         */
        def getAllByGranteeIds(granteeIds: Seq[String], privilegeClass: Class[_]): Seq[Privilege[_ <: Entity]]

        /**
         * Returns all privileges granted to the specified grantee.
         * @param granteeId ID of the privilege grantee.
         */
        def getAllByGranteeId(granteeId: String): Seq[Privilege[_ <: Entity]]

        /**
         * Gets a list of privileges of the specified type to the specified object.
         * @param objId ID of the privileged object
         * @param privilegeClass Class of the privilege.
         */
        def getAllByObjectIdAndPrivilegeClass(objId: String, privilegeClass: Class[_]): Seq[Privilege[_ <: Entity]]
    }

    /**
     * Defines operations of repository accessing analyses
     */
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

    /**
     * Defines operations of repository accessing ontology customizations
     */
    trait CustomizationRepository
        extends Repository[Customization]
        with NamedEntityRepository[Customization]
        with OptionallyOwnedEntityRepository[Customization]
        with ShareableEntityRepository[Customization]
    {
        /**
         * Persists given ClassCustomization
         * @param classCustomization ClassCustomization to persist
         */
        def persistClassCustomization(classCustomization: AnyRef)

        /**
         * Persists given PropertyCustomization
         * @param propertyCustomization PropertyCustomization to persist
         */
        def persistPropertyCustomization(propertyCustomization: AnyRef)

        /**
         * Removes an entity with the specified ID from the repository.
         * @param id Id of the entity to remove.
         * @return True if the entity was removed, false otherwise.
         */
        def removeClassCustomizationById(id: String): Boolean

        /**
         * Removes an entity with the specified ID from the repository.
         * @param id Id of the entity to remove.
         * @return True if the entity was removed, false otherwise.
         */
        def removePropertyCustomizationById(id: String): Boolean
    }

    /**
     * Defines operations of repository accessing plugins
     */
    trait PluginRepository
        extends Repository[Plugin]
        with NamedEntityRepository[Plugin]
        with OptionallyOwnedEntityRepository[Plugin]
        with ShareableEntityRepository[Plugin]

    /**
     * Defines operations of repository accessing data sources
     */
    trait DataSourceRepository
        extends Repository[DataSource]
        with NamedEntityRepository[DataSource]
        with OptionallyOwnedEntityRepository[DataSource]
        with ShareableEntityRepository[DataSource]
    {
        /**
         * Persists ParameterValue of DataSource
         * @param parameterValue ParameterValue to persist
         */
        def persistParameterValue(parameterValue: ParameterValue[_])
    }

    /**
     * Defines operations of repository accessing prefixes
     */
    trait PrefixRepository
        extends Repository[Prefix]
        with NamedEntityRepository[Prefix]
        with OptionallyOwnedEntityRepository[Prefix]
    {
        /**
         * Gets all public prefixes available to user - default (unowned) and his own.
         * @param userId Id of a user to search prefixes for
         * @return Returns prefixes available to user
         */
        def getAllAvailableToUser(userId: Option[String]): Seq[Prefix]
    }

    trait AnalysisResultRepository extends Repository[AnalysisResult]
    {
        def storeResult(analysisDescription: AnalysisResult, embeddedHash: Option[String] = None)

        def getResult(evaluationId: String, analysisId: String): Option[AnalysisResult]

        def deleteResult(evaluationId: String, analysisId: String)

        def updateTimestamp(evaluationId: String)

        def byEvaluationId(evaluationId: String) : Option[AnalysisResult]

        def exists(evaluationId: String): Boolean

        def getAllAvailableToUser(userId: Option[String]): Seq[AnalysisResult]
    }

    trait EmbeddingDescriptionRepository extends Repository[EmbeddingDescription]
    {
        def createEmbeddedUriHash(analysisResult: entities.AnalysisResult): EmbeddingDescription

        def getEmbeddedUriHash(analysisResultId: String): Option[EmbeddingDescription]

        def getAllAvailableToUser(userId: Option[String]): Seq[EmbeddingDescription]

        def getEmbeddedById(embedId: String): Option[EmbeddingDescription]

        def getEmbeddedByUriHash(uriHash: String): Option[EmbeddingDescription]

        def removeByAnalysisId(id: String): Boolean

        def setViewPlugin(id: String, visualPlugin: String): Option[EmbeddingDescription]

        def updateEvaluation(uriHash: String, analysisResultId: String)
    }

    /**
     * A registry providing repositories by entity class names.
     * @param repositoriesByClass The repositories to store in the registry indexed by classes whose instances the
     *                            repositories contain.
     */
    class RepositoryRegistry(repositoriesByClass: Map[Class[_], Repository[_]])
    {
        private val repositoriesByClassName = repositoriesByClass.map {r =>
            cz.payola.common.Entity.getClassName(r._1) -> r._2
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
         * Returns a repository by an entity class.
         * @param entityClass Class whose instances the repository contains.
         */
        def apply(entityClass: Class[_]): Repository[_] = {
            apply(cz.payola.common.Entity.getClassName(entityClass))
        }

        /**
         * Returns a repository that contains the specified entity.
         * @param entity The entity that is stored in the repository.
         */
        def apply(entity: Entity): Repository[_] = {
            apply(entity.className)
        }
    }

}
