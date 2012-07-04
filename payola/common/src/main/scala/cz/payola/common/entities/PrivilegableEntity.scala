package cz.payola.common.entities

import scala.collection._
import cz.payola.common.entities.privileges._
import cz.payola.common.entities.plugins.DataSource
import cz.payola.common.entities.settings.ontology.Customization
import scala.Seq

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends Entity
{
    /** Type of the privileges. */
    type PrivilegeType <: Privilege[_]

    protected val _privileges = mutable.ArrayBuffer[PrivilegeType]()

    /** Privileges of the user. */
    def privileges: immutable.Seq[PrivilegeType] = _privileges.toList

    /** Returns the analyses that are accessible for the user directly via his privileges. */
    def accessibleAnalyses: immutable.Seq[Analysis] = {
        privileges.toList.collect { case p: AccessAnalysisPrivilege => p.obj }
    }

    /** Returns the data sources that are accessible for the user directly via his privileges. */
    def accessibleDataSources: immutable.Seq[DataSource] = {
        privileges.toList.collect { case p: AccessDataSourcePrivilege => p.obj }
    }

    /** Returns the ontology customizations that are accessible for the user directly via his privileges. */
    def accessibleOntologyCustomizations: immutable.Seq[settings.ontology.Customization] = {
        privileges.toList.collect { case c: UseOntologyCustomizationPrivilege => c.obj }
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
