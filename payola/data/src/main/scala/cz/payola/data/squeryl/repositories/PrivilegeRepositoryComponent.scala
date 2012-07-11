package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.Privilege
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PaginationInfo
import scala.collection.mutable

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository
    {
        private val representationRepository = new LazyTableRepository[PrivilegeDbRepresentation](schema.privileges,
            PrivilegeDbRepresentation)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Privilege[_]] = Seq()

        def getByIds(ids: Seq[String]): Seq[Privilege[_]] = schema.wrapInTransaction {
            val privileges = representationRepository.getByIds(ids)

            val repositories = mutable.Map[Repository[_], mutable.HashSet[String]]()

            // Fill repositories map with values (Repository -> list of entity IDs to select)
            privileges.foreach { p =>
                // Add grantee
                val granteeRepository = repositoryRegistry(p.granteeClassName)
                repositories.getOrElseUpdate(granteeRepository, mutable.HashSet.empty[String]) += p.granteeId

                // Add object
                val objectRepository = repositoryRegistry(p.objectClassName)
                repositories.getOrElseUpdate(objectRepository, mutable.HashSet.empty[String]) += p.objectId

                // Add owner (user)
                repositories.getOrElseUpdate(userRepository, mutable.HashSet.empty[String]) += p.granterId
            }

            // Create Map (EntityId -> Entity)
            val entities = repositories.par.flatMap{r =>
                r._1.getByIds(r._2.toSeq).map(e => (e.asInstanceOf[cz.payola.domain.Entity].id, e))
            }

            privileges.map{ p =>
                // Get granter, grantee, object from entities set
                val granter = entities(p.granterId).asInstanceOf[java.lang.Object]
                val grantee = entities(p.granteeId).asInstanceOf[java.lang.Object]
                val obj = entities(p.objectId).asInstanceOf[java.lang.Object]

                // Get and fill Privilege constructor (with 4 parameters)
                val privilegeClass = java.lang.Class.forName(p.privilegeClass)
                val constructor = privilegeClass.getConstructors.find(_.getParameterTypes().size == 4).get
                val arguments = List(granter, grantee, obj, p.id).toArray

                // Instantiate the privilege
                constructor.newInstance(arguments: _*).asInstanceOf[Privilege[_]]
            }
        }

        def persist(entity: AnyRef): Privilege[_] = schema.wrapInTransaction[Privilege[_]] {
            representationRepository.persist(entity)

            // The entity was successfully persisted therefore it must be a privilege.
            entity.asInstanceOf[Privilege[_]]
        }

        def removeById(id: String) = schema.wrapInTransaction{
            representationRepository.removeById(id)
        }

        def getCount: Long = schema.wrapInTransaction {
            representationRepository.getCount
        }

        def getAllGrantedTo[B](granteeIds: Seq[String], privilegeClass: Class[_]): Seq[B] = {
            // TODO implement
            Nil
        }

        // TODO remove
        def getPrivilegedObjectIds(granteeId: String, privilegeClass: Class[_], objectClass: Class[_]): Seq[String] = {
            schema.wrapInTransaction {
                from(representationRepository.table)(p =>
                    where(p.granteeId === granteeId and
                        p.privilegeClass === privilegeClass.getName and
                        p.objectClassName === repositoryRegistry.getClassName(objectClass)
                    )
                    select(p.objectId)
                ).toList
            }
        }
    }
}
