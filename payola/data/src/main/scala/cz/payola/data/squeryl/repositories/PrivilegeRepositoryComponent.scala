package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.Privilege
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        private val _repository = new TableRepository[PrivilegeDbRepresentation](schema.privileges, PrivilegeDbRepresentation)

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
