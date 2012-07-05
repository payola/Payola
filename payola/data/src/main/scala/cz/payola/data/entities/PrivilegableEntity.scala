package cz.payola.data.entities

import scala.collection.immutable
import cz.payola.data.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.dao._
import cz.payola.domain.entities.privileges._

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.common.entities.PrivilegableEntity {

    override def accessibleAnalyses: immutable.Seq[cz.payola.common.entities.Analysis] = {
        val analysisDao = new AnalysisDAO

        _loadObjectIds(
            classOf[AccessAnalysisPrivilege].toString,
            classOf[cz.payola.common.entities.Analysis].toString
        ).flatMap(analysisDao.getById(_))
    }

    override def accessibleDataSources: immutable.Seq[cz.payola.common.entities.plugins.DataSource] = {
        val dataSourceDao = new DataSourceDAO

        _loadObjectIds(
            classOf[AccessDataSourcePrivilege].toString,
            classOf[cz.payola.common.entities.plugins.DataSource].toString
        ).flatMap(dataSourceDao.getById(_))
    }

    /* TODO: customization will be implement later
    override def accessibleOntologyCustomizations: immutable.Seq[cz.payola.common.entities.settings.ontology.Customization] = {
        _loadObjectIds(
            UseOntologyCustomizationPrivilege.getClass.toString,
            cz.payola.common.entities.settings.ontology.Customization.getClass.toString
        )
    }
    */


    private def _loadObjectIds(privilegeClass: String, objectClass: String) = {
        new PrivilegeDAO().loadObjectIds(this.id, privilegeClass, objectClass)
    }

    protected override def storePrivilege(granter: cz.payola.common.entities.User,  privilege: PrivilegeType) {
        // TODO: injection
        new PrivilegeDAO().persist(PrivilegeDbRepresentation(privilege, granter, this))
        super.storePrivilege(granter, privilege)
    }

    protected override def discardPrivilege(privilege: PrivilegeType) {
        // TODO: injection
        new PrivilegeDAO().removeById(privilege.id)
        super.discardPrivilege(privilege)
    }
}
