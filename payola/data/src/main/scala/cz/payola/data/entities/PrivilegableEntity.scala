package cz.payola.data.entities

import scala.collection._
import cz.payola.common.entities.privileges._
import cz.payola.common.entities.plugins.DataSource

/**
  * An entity that may be granted privileges.
  */
trait PrivilegableEntity extends cz.payola.common.entities.PrivilegableEntity {

    override def accessibleAnalyses: immutable.Seq[Analysis] = {
        null
    }

    override def accessibleDataSources: immutable.Seq[DataSource] = {
        null
    }

    override def accessibleOntologyCustomizations: immutable.Seq[cz.payola.common.entities.settings.ontology.Customization] = {
        null
    }

    protected override def storePrivilege(privilege: PrivilegeType) {

        super.storePrivilege(privilege)
    }

    protected override def discardPrivilege(privilege: PrivilegeType) {
        super.discardPrivilege(privilege)
    }
}
