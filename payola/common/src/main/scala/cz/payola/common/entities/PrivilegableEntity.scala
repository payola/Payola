package cz.payola.common.entities

import scala.collection.mutable
import cz.payola.common.entities.privileges._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.common.entities.settings.ontology.Customization

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity
{
    /** Type of the privileges. */
    type PrivilegeType <: Privilege[_]

    protected val _privileges = mutable.ArrayBuffer[PrivilegeType]()

    /** Privileges of the user. */
    def privileges: Seq[PrivilegeType] = _privileges

    /** Returns the analyses that are accessible for the user directly via his privileges. */
    def accessibleAnalyses: Seq[Analysis] = privileges.collect { case p: AccessAnalysisPrivilege => p.obj }

    /** Returns the data sources that are accessible for the user directly via his privileges. */
    def accessibleDataSources: Seq[DataSource] = privileges.collect { case p: AccessDataSourcePrivilege => p.obj }

    /** Returns the ontology customizations that are accessible for the user directly via his privileges. */
    def accessibleOntologyCustomizations: Seq[settings.ontology.Customization] = {
        privileges.collect { case c: UseOntologyCustomizationPrivilege => c.obj }
    }

    /**
      * Stores the specified privileges to the users.
      * @param privilege The privilege to store.
      */
    protected def storePrivilege(privilege: PrivilegeType) {
        _privileges += privilege
    }

    /**
      * Discards the privileges from the user. Complementary operation to store.
      * @param privilege The privilege to discard.
      */
    protected def discardPrivilege(privilege: PrivilegeType) {
        _privileges -= privilege
    }
}
