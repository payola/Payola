package cz.payola.model

import generic.ConcreteEntity
import cz.payola.common
import cz.payola.common.model.SharePrivilege

class AnalysisShare (val analysis: Analysis, var privilege: Int) extends common.model.AnalysisShare with ConcreteEntity {
    require(analysis != null, "Analysis cannot be null!")
    require(privilege == SharePrivilege.IncludingData || privilege == SharePrivilege.ResultOnly, "Privilige unknown!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilege.ResultOnly)

}
