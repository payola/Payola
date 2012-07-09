package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.{Privilege, User, Group, Analysis}
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities.plugins.DataSource
import cz.payola.domain.Entity

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        private val _repository = new TableRepository[PrivilegeDbRepresentation](schema.privileges, PrivilegeDbRepresentation)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Privilege[_]] = Seq()

        def getById(id: String): Option[Privilege[_]] = {
            val privilegeQuery = _repository.table.where(e => e.id === id)

            val privilegeDb = _repository.evaluateSingleResultQuery(privilegeQuery)

            // If not found ...
            if (privilegeDb.isEmpty){
                None
            }
            else {
                // ... instantiate otherwise
                val objectRepository = repositoryRegistry(privilegeDb.get.objectClass)
                val granteeRepository = repositoryRegistry(privilegeDb.get.granteeClass)

                val objectOption = objectRepository.getById(privilegeDb.get.objectId)
                val granteeOption = granteeRepository.getById(privilegeDb.get.granteeId)
                val granterOption = userRepository.getById(privilegeDb.get.granterId)

                // If object or grantee or granter not found
                if (objectOption.isEmpty || granteeOption.isEmpty || granterOption.isEmpty) {
                    None
                }
                else {
                    // Create instance
                    val privilegeClass = java.lang.Class.forName(privilegeDb.get.privilegeClass)

                    val constructor = privilegeClass.getConstructors.find(_.getParameterTypes().size == 2).get
                    val arguments = List(granterOption.get, granteeOption.get, objectOption.get, privilegeDb.get.id)

                    // Instantiate the privilege
                    Some(constructor.newInstance(arguments).asInstanceOf[Privilege[_ <: Entity]])
                }

            }
        }

        def persist(entity: AnyRef): Privilege[_] = {
            _repository.persist(entity)

            // The entity was successfully persisted therefore it must be a privilege.
            entity.asInstanceOf[Privilege[_]]
        }

        def removeById(id: String) = _repository.removeById(id)

        def getPrivilegedObjectIds(granteeId: String, privilegeClass: Class[_], objectClass: Class[_]): Seq[String] = {
            val query = from(_repository.table)(p =>
                where(p.granteeId === granteeId and
                    p.privilegeClass === privilegeClass.getName and
                    p.objectClass === repositoryRegistry.getClassName(objectClass)
                )
                select(p.objectId)
            )

            _repository.evaluateCollectionResultQuery(query)
        }

        def getCount: Int = _repository.getAll().size
    }
}
