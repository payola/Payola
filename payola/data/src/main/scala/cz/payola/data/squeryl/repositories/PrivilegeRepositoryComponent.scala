package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.{Privilege, User, Group, Analysis}
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities.plugins.DataSource

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        private val objectRepositories = Map(
            PrivilegeDbRepresentation.stripClassName(classOf[Analysis].getName) -> analysisRepository,
            PrivilegeDbRepresentation.stripClassName(classOf[DataSource].getName) -> dataSourceRepository
        )

        private val granteeRepositories = Map(
            PrivilegeDbRepresentation.stripClassName(classOf[User].getName) -> userRepository,
            PrivilegeDbRepresentation.stripClassName(classOf[Group].getName) -> groupRepository
        )

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
                val objectRepository: Repository[_] = objectRepositories.get(privilegeDb.get.objectClass).get

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
                    Some(constructor.newInstance(constructorArguments).asInstanceOf[Privilege[_]])
                }

            }
        }

        def persist(entity: AnyRef) {
            _repository.persist(entity)
        }

        def removeById(id: String) = _repository.removeById(id)

        def getPrivilegedObjectIds(granteeId: String, privilegeClass: Class[_], objectClass: Class[_]): Seq[String] = {
            val query = from(_repository.table)(p =>
                where(p.granteeId === granteeId and
                    p.privilegeClass === PrivilegeDbRepresentation.stripClassName(privilegeClass.toString) and
                    p.objectClass === PrivilegeDbRepresentation.stripClassName(objectClass.toString)
                )
                select(p.objectId)
            )

            _repository.evaluateCollectionResultQuery(query)
        }

        def getCount: Int = _repository.getAll().size
    }
}
