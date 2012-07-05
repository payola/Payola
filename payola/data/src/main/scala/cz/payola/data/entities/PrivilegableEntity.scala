package cz.payola.data.entities

import scala.collection.immutable
import cz.payola.data.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.dao._
import cz.payola.domain.entities.privileges._

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.domain.entities.PrivilegableEntity
{ self: cz.payola.domain.entities.Entity =>

    override def grantedAnalyses: immutable.Seq[cz.payola.common.entities.Analysis] = {
        val analysisDao = new AnalysisDAO

        _loadObjectIds(
            classOf[AccessAnalysisPrivilege].toString,
            classOf[cz.payola.common.entities.Analysis].toString
        ).flatMap(analysisDao.getById(_)).toList
    }

    override def grantedDataSources: immutable.Seq[cz.payola.common.entities.plugins.DataSource] = {
        val dataSourceDao = new DataSourceDAO

        val r = _loadObjectIds(
            classOf[AccessDataSourcePrivilege].toString,
            classOf[cz.payola.common.entities.plugins.DataSource].toString
        ).flatMap(dataSourceDao.getById(_)).toList

        r
    }

    /* TODO: customization will be implement later
    override def grantedOntologyCustomizations: immutable.Seq[cz.payola.common.entities.settings.ontology.Customization] = {
        _loadObjectIds(
            UseOntologyCustomizationPrivilege.getClass.toString,
            cz.payola.common.entities.settings.ontology.Customization.getClass.toString
        )
    }
    */

    private def _loadObjectIds(privilegeClass: String, objectClass: String) = {
        new PrivilegeDAO().loadPrivileges(
            this.id,
            PrivilegeDbRepresentation.stripClassName(privilegeClass),
            PrivilegeDbRepresentation.stripClassName(objectClass)
        ).map(_.objectId)
    }

    override def grantPrivilege(privilege: PrivilegeType, granter: cz.payola.domain.entities.User) {
        // Call domain method to preserve functionality
        storePrivilege(privilege)

        // TODO: injection
        new PrivilegeDAO().persist(PrivilegeDbRepresentation(privilege, granter, this))
    }


    override def removePrivilege(privilege: PrivilegeType, granter: cz.payola.domain.entities.User) = {
        // Call domain method to preserve functionality
        discardPrivilege(privilege)

        // TODO: injection
        if (new PrivilegeDAO().removeById(privilege.id) == true) {
            Some(privilege)
        }
        else {
            None
        }

    }
}
