package cz.payola.model

import generic.ConcreteModelObject
import cz.payola.common.model.SharePrivilege._

class AnalysisShare (var analysis: Analysis, var privilege: SharePrivilege) extends ConcreteModelObject with cz.payola.common.model.AnalysisShare {
    require(analysis != null, "Analysis cannot be null!")
    require(privilege != null, "Privilige cannot be null!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilegeResultOnly)

}
