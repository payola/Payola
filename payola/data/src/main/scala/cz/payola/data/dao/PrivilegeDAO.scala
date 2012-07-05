package cz.payola.data.dao

import scala.collection.immutable
import cz.payola.data.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PayolaDB
import org.squeryl.PrimitiveTypeMode._

class PrivilegeDAO extends EntityDAO[PrivilegeDbRepresentation](PayolaDB.privileges)
{
    /**
      * Makes [[cz.payola.data.dao.EntityDAO.persist()]] method accessible.
      *
      * @param privilege - [[cz.payola.data.entities.privileges.PrivilegeDbRepresentation]] of [[cz.payola.common.entities.Privilege]]
      * @return Returns persisted privilege
      */
    override def persist(privilege: PrivilegeDbRepresentation) = {
        super.persist(privilege)
    }

    /**
      * Loads IDs of objects that grantee has privilege to.
      *
      * @param granteeId - id of [[cz.payola.common.entities.PrivilegableEntity]] that has privilege
      * @param privilegeClass - class of [[cz.payola.common.entities.Privilege]] assigned to grantee Entity
      * @param objectClass - class of objects that are subjects of the Privilege
      *
      * @return Returns list of IDs
      */
    def loadObjectIds(granteeId: String, privilegeClass: String, objectClass: String): immutable.Seq[String] = {
        val query = table.where(p => 
            p.granteeId === granteeId and p.privilegeClass === privilegeClass and p.objectClass === objectClass
        )
        
        evaluateCollectionResultQuery(query).map(_.objectId).asInstanceOf[immutable.Seq[String]]
    }
}
