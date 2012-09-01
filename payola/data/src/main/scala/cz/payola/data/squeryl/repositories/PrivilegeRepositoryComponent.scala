package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.domain.entities._
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import scala.collection.mutable
import cz.payola.domain.Entity
import cz.payola.data.PaginationInfo
import org.squeryl.PrimitiveTypeMode._

/**
 * Provides repository to access persisted privileges
 */
trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    private type PrivilegeType = Privilege[_ <: Entity]

    /**
     * A repository to access persisted privileges
     */
    lazy val privilegeRepository = new PrivilegeRepository
    {
        private val representationRepository =
            new LazyTableRepository[PrivilegeDbRepresentation](schema.privileges, PrivilegeDbRepresentation)

        def getByIds(ids: Seq[String]): Seq[PrivilegeType] = schema.wrapInTransaction {
            instantiate(representationRepository.getByIds(ids))
        }

        def getAll(pagination: Option[PaginationInfo] = None) = schema.wrapInTransaction {
            instantiate(representationRepository.getAll(pagination))
        }

        def persist(entity: AnyRef): PrivilegeType = schema.wrapInTransaction[PrivilegeType] {
            representationRepository.persist(entity)

            // The entity was successfully persisted therefore it must be a privilege.
            entity.asInstanceOf[PrivilegeType]
        }

        def removeById(id: String) = schema.wrapInTransaction {
            representationRepository.removeById(id)
        }

        def getCount: Long = schema.wrapInTransaction {
            representationRepository.getCount
        }

        def getAllByGranteeIds(granteeIds: Seq[String], privilegeClass: Class[_]): Seq[PrivilegeType] = {
            _getByGrantee(granteeIds, Some(privilegeClass))
        }

        def getAllByGranteeId(granteeId: String): Seq[PrivilegeType] = {
            _getByGrantee(List(granteeId), None)
        }

        def getAllByObjectIdAndPrivilegeClass(objId: String, privilegeClass: Class[_]): Seq[PrivilegeType] = {
            schema.wrapInTransaction {
                instantiate(
                    representationRepository.selectWhere(p =>
                        p.objectId === objId
                            and p.privilegeClass === privilegeClass.getName)
                )
            }
        }

        private def _getByGrantee(granteeIds: Seq[String], privilegeClass: Option[Class[_]]): Seq[PrivilegeType] =
            schema.wrapInTransaction {
                instantiate(
                    representationRepository.selectWhere(p =>
                        p.granteeId in granteeIds
                            and p.privilegeClass === privilegeClass.map(_.getName).getOrElse(p.privilegeClass).toString)
                )
            }

        private def instantiate(privileges: Seq[PrivilegeDbRepresentation]): Seq[PrivilegeType] =
            schema.wrapInTransaction {
                // Repositories map with (Repository -> list of entity IDs to select)
                val repositories = mutable.Map[Repository[_], mutable.HashSet[String]]()

                // Fill the map
                privileges.foreach {p =>
                // Add grantee
                    val granteeRepository = repositoryRegistry(p.granteeClassName)
                    repositories.getOrElseUpdate(granteeRepository, mutable.HashSet.empty[String]) += p.granteeId

                    // Add object
                    val objectRepository = repositoryRegistry(p.objectClassName)
                    repositories.getOrElseUpdate(objectRepository, mutable.HashSet.empty[String]) += p.objectId

                    // Add owner (user)
                    repositories.getOrElseUpdate(userRepository, mutable.HashSet.empty[String]) += p.granterId
                }

                // Create Map (EntityId -> Entity) to simplify getting entities from Db
                val entities = repositories.par.flatMap {r =>
                    r._1.getByIds(r._2.toSeq).map(e => (e.asInstanceOf[cz.payola.domain.Entity].id, e))
                }

                privileges.map {p =>
                // Get granter, grantee, object from entities set
                    val granter = entities(p.granterId).asInstanceOf[java.lang.Object]
                    val grantee = entities(p.granteeId).asInstanceOf[java.lang.Object]
                    val obj = entities(p.objectId).asInstanceOf[java.lang.Object]

                    // Get and fill Privilege constructor (with 4 parameters)
                    val privilegeClass = java.lang.Class.forName(p.privilegeClass)
                    val constructor = privilegeClass.getConstructors.find(_.getParameterTypes.size == 4).get
                    val arguments = List(granter, grantee, obj, p.id).toArray

                    // Instantiate the privilege
                    constructor.newInstance(arguments: _*).asInstanceOf[PrivilegeType]
                }
            }
    }
}
