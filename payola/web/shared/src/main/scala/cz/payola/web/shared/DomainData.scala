package cz.payola.web.shared

import cz.payola.domain.entities.User
import s2js.compiler._
import cz.payola.common.entities.Group

@remote object DomainData
{
    @async @secured def searchMembersAvailableForGroup(groupId: String, term: String, owner: User = null)(successCallback: (Seq[User] => Unit))
        (failCallback: (Throwable => Unit)) {
        val group = Payola.model.groupModel.getById(groupId).getOrElse{
            throw new Exception("Group not found.")
        }

        val users = Payola.model.groupModel.findAvailableMembers(group, owner, term)
        successCallback(users)
    }

    @async @secured def searchUsers(term: String, user: User = null)(successCallback: (Seq[User] => Unit))
        (failCallback: (Throwable => Unit)) {
        val users = Payola.model.userModel.getByNameLike(term)
        successCallback(users)
    }

    @async @secured def searchGroups(term: String, user: User = null)(successCallback: (Seq[Group] => Unit))
        (failCallback: (Throwable => Unit)) {

        val groups = user.ownedGroups.filter{ g =>
            g.name.contains(term)
        }

        successCallback(groups)
    }

    def searchDataSources(needle: String) = {
    }
}
