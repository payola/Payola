package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola

@remote @secured object PrefixManager
{
    /**
     * SYNCHRONOUS call to get all available prefixes for user
     * @param user User
     * @return Returns ordered collection of prefixes (user prefixes first, global prefixes after them)
     */
    def getAvailablePrefixes(user: Option[User] = null) : Seq[Prefix] = {
        // Ensures that first comes the user.defined prefixes, then comes global prefixes (important for translation)
        Payola.model.prefixModel.getAllAvailableToUser(user.map(_.id)).sortBy(p => p.owner.map(_.id)).reverse
    }
}
