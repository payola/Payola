package cz.payola.model

/**
 * User: Krystof Vasa
 * Date: 25.11.11
 * Time: 17:02
 */

object SharePrivilege extends Enumeration {
    type SharePrivilege = Value
    val SharePrivilegeResultOnly, SharePrivilegeIncludingData = Value
}

import SharePrivilege._

class AnalysisShare (var analysis: Analysis, var privilege: SharePrivilege) {
    assert(analysis != null, "Analysis cannot be null!")
    assert(privilege != null, "Privilige cannot be null!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilegeResultOnly)
}
