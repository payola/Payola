package cz.payola.model

import generic.ConcreteModelObject
import cz.payola.common.model.SharePriviledges._

class AnalysisShare (var analysis: Analysis, var privilege: Int) extends cz.payola.common.model.AnalysisShare with ConcreteModelObject {
    require(analysis != null, "Analysis cannot be null!")
    require(privilege == SharePrivilegeIncludingData || privilege == SharePrivilegeResultOnly, "Privilige unknown!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilegeResultOnly)

}
