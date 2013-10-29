package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola
import cz.payola.domain.net.Downloader

@remote @secured object PrefixManager
{
    /**
     * SYNCHRONOUS call to get all available prefixes for user
     * @param user User
     * @return Returns ordered collection of prefixes (user prefixes first, global prefixes after them)
     */
    def getAvailablePrefixes(user: Option[User] = null) : Seq[Prefix] = {
        // Ensures that first comes the user.defined prefixes, then comes global prefixes (important for translation)
        // Secondary are ordered by length descendant to ensure that more specific (longer) uri is used first
        Payola.model.prefixModel.getAllAvailableToUser(user.map(_.id)).sortBy(p => p.url.length).sortBy(p => p.owner.map(_.id)).reverse
    }

    @async def findUnknownPrefix(prefix: String, user: User = null)(successCallback: String => Unit)(errorCallback: Throwable => Unit) {
        try
        {
            val result = new Downloader("http://prefix.cc/%s.file.txt".format(prefix)).result
            val url = result.replaceFirst(prefix, "").trim()

            Payola.model.prefixModel.create(prefix, url, None)

            successCallback(url)
        }
        catch {
            case e: Throwable => errorCallback(e)
            case _ => errorCallback(new Exception("Prefix %s not found on prefix.cc".format(prefix)))
        }
    }
}
