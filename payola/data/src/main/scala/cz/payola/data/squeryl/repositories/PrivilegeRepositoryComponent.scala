package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.Privilege
import cz.payola.data.PaginationInfo
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.common.Entity

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        private val _repository = new TableRepository[PrivilegeDbRepresentation](schema.privileges, PrivilegeDbRepresentation)

        def getById(id: String): Option[Privilege[_]] = None

        def removeById(id: String): Boolean = _repository.removeById(id)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Privilege[_]] = Nil

        def persist(entity: AnyRef): Privilege[_] =  {
            _repository.persist(entity)

            // TODO: no way to instantiate Privilege from its Db representation
            null
        }

        def getPrivilegedObjectIds(granteeId: String, privilegeClass: String, objectClass: String): Seq[String] = {
            val query = from(_repository.table)(p =>
                where(p.granteeId === granteeId and p.privilegeClass === privilegeClass and p.objectClass === objectClass)
                select(p.objectId)
            )

            _repository.evaluateCollectionResultQuery(query)
        }

        def getPrivilegesCount(): Int = _repository.getAll().size
    }
}
