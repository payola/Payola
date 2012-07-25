package cz.payola.web.shared

import cz.payola.domain.entities.User
import s2js.compiler._
import cz.payola.common.entities._

@remote @secured object DomainData
{
    @async def searchMembersAvailableForGroup(groupId: String, term: String, owner: User = null)(successCallback: (Seq[User] => Unit))
        (failCallback: (Throwable => Unit)) {
        val group = Payola.model.groupModel.getById(groupId).getOrElse{
            throw new Exception("Group not found.")
        }

        val users = Payola.model.groupModel.findAvailableMembers(group, owner, term)
        successCallback(users)
    }

    @async def searchUsers(term: String, user: User = null)(successCallback: (Seq[User] => Unit))
        (failCallback: (Throwable => Unit)) {
        val users = Payola.model.userModel.getByNameLike(term)
        successCallback(users)
    }

    @async def searchGroups(term: String, user: User = null)(successCallback: (Seq[Group] => Unit))
        (failCallback: (Throwable => Unit)) {

        val groups = user.ownedGroups.filter{ g =>
            g.name.contains(term)
        }

        successCallback(groups)
    }

    @async def getAnalysisById(analysisId: String, user: Option[User] = None)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {

        val analysis = Payola.model.analysisModel.getAccessibleToUserById(user,analysisId).getOrElse{
            throw new Exception("Analysis not found.")
        }

        successCallback(analysis)
    }
}
