
package cz.payola.model

/**
 * Created by IntelliJ IDEA.
 * User: Krystof Vasa
 * Date: 21.11.11
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */

import scala.collection.mutable._

class Analysis (u: User) {
    private var _owner: User = null
    setOwner(u)


    def isOwnedByUser(u: User): Boolean = owner == u
    def owner: User = _owner
    def owner_=(u: User) = {
        assert(u != null, "Analysis has to have an owner!")
        val oldOwner = _owner

        _owner = u
        _owner.addAnalysis(this)
        
        if (oldOwner != null)
            oldOwner.removeAnalysis(this)
    }
    def setOwner(u: User) = owner_=(u)

}
