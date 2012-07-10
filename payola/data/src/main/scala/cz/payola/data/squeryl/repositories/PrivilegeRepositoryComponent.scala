package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.{Privilege, User, Group, Analysis}
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities.plugins.DataSource
import cz.payola.domain.Entity
import scala.collection.mutable
import cz.payola.data.squeryl.entities.PersistableEntity

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        private val representationRepository = new LazyTableRepository[PrivilegeDbRepresentation](schema.privileges,
            PrivilegeDbRepresentation)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Privilege[_]] = Seq()

        def getByIds(ids: Seq[String]): Seq[Privilege[_]] = {
            val privileges = representationRepository.getByIds(ids)

            val repositories = mutable.Map[Repository[_], mutable.HashSet[String]]()

            // Fill repositories map with values (Repository -> list of entity IDs to select)
            privileges.foreach { p =>
                // Add grantee
                val granteeRepository = repositoryRegistry(p.granteeClass)
                repositories.getOrElseUpdate(granteeRepository, mutable.HashSet.empty[String]) += p.granteeId

                // Add object
                val objectRepository = repositoryRegistry(p.granteeClass)
                repositories.getOrElseUpdate(granteeRepository, mutable.HashSet.empty[String]) += p.granteeId

                // Add owner
                repositories.getOrElseUpdate(userRepository, mutable.HashSet.empty[String]) += p.granteeId
            }

            val entities = repositories.par.flatMap(r => r._1.getByIds(r._2.toSeq).map(e => (e.asInstanceOf[PersistableEntity].id, e)))

            Seq()

            /*
            // If not found ...
            if (entities.isEmpty){
                None
            }
            else {
                // ... instantiate otherwise
                val objectRepository = repositoryRegistry(privilegeDb.get.objectClass)

                val objectOption = objectRepository.getById(privilegeDb.get.objectId)

                // Object not found
                if (objectOption.isEmpty) {
                    None
                }
                else {
                    // Create instance
                    val privilegeClass = java.lang.Class.forName(privilegeDb.get.privilegeClass)

                    val constructor = privilegeClass.getConstructors.find(_.getParameterTypes().size == 2).get
                    val constructorArguments = List(objectOption.get, privilegeDb.get.id)

                    // Instantiate the privilege
                    Some(constructor.newInstance(constructorArguments).asInstanceOf[Privilege[_ <: Entity]])
                }

            }
            */
        }

        def persist(entity: AnyRef): Privilege[_] = {
            representationRepository.persist(entity)

            // The entity was successfully persisted therefore it must be a privilege.
            entity.asInstanceOf[Privilege[_]]
        }

        def removeById(id: String) = representationRepository.removeById(id)

        def getCount: Long = representationRepository.getCount

        def getPrivilegedObjectIds(granteeId: String, privilegeClass: Class[_], objectClass: Class[_]): Seq[String] = {
            val query = from(representationRepository.table)(p =>
                where(p.granteeId === granteeId and
                    p.privilegeClass === privilegeClass.getName and
                    p.objectClass === repositoryRegistry.getClassName(objectClass)
                )
                select(p.objectId)
            )

            representationRepository.evaluateCollectionResultQuery(query)
        }
    }
}
