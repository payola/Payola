package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl._
import cz.payola.domain.entities.Privilege
import cz.payola.data.PaginationInfo

trait PrivilegeRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val privilegeRepository = new PrivilegeRepository[Privilege[_]]
    {
        def getById(id: String): Option[Privilege[_]] = None // TODO

        def removeById(id: String): Boolean = false // TODO

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Privilege[_]] = Nil // TODO

        def persist(entity: AnyRef): Privilege[_] = null // TODO

        /**
          * Loads [[cz.payola.common.entities.Privilege]] by privileged object class and privileged greantee
          *
          * @param granteeId - id of [[cz.payola.common.entities.PrivilegableEntity]] that has privilege
          * @param privilegeClass - stripped class of [[cz.payola.common.entities.Privilege]] assigned to grantee Entity
          * @param objectClass - stripped class of objects that are subjects of the Privilege
          *
          * @return Returns list of Privileges
          */
        def getPrivilegeObjectIds(granteeId: String, privilegeClass: String, objectClass: String): Seq[String] = {
            /*TODO val query = from(table)(p =>
                where(p.granteeId === granteeId and p.privilegeClass === privilegeClass and p.objectClass === objectClass)
                select(p.objectId)
            )
            evaluateCollectionResultQuery(query)*/
            Nil
        }
    }
}
