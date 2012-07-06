package cz.payola.data.squeryl.entities

import scala.collection.immutable
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.squeryl.repositories._
import cz.payola.domain.entities.privileges._
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.domain.entities.PrivilegableEntity
{
    self: PersistableEntity =>

    val context: SquerylDataContextComponent

    override def grantedAnalyses: immutable.Seq[cz.payola.common.entities.Analysis] = {
        _loadObjectIds(
            classOf[AccessAnalysisPrivilege].toString,
            classOf[cz.payola.common.entities.Analysis].toString
        ).flatMap(context.analysisRepository.getById(_)).toList
    }

    override def grantedDataSources: immutable.Seq[cz.payola.common.entities.plugins.DataSource] = {
        _loadObjectIds(
            classOf[AccessDataSourcePrivilege].toString,
            classOf[cz.payola.common.entities.plugins.DataSource].toString
        ).flatMap(context.dataSourceRepository.getById(_)).toList
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
        context.privilegeRepository.getPrivilegeObjectIds(id, PrivilegeDbRepresentation.stripClassName(privilegeClass),
            PrivilegeDbRepresentation.stripClassName(objectClass))
    }

    override def grantPrivilege(privilege: PrivilegeType, granter: cz.payola.domain.entities.User) {
        // Call domain method to preserve functionality
        storePrivilege(privilege)

        context.privilegeRepository.persist(PrivilegeDbRepresentation(privilege, granter, this))
    }


    override def removePrivilege(privilege: PrivilegeType, granter: cz.payola.domain.entities.User) = {
        // Call domain method to preserve functionality
        discardPrivilege(privilege)

        if (context.privilegeRepository.removeById(privilege.id)) Some(privilege) else None
    }
}
