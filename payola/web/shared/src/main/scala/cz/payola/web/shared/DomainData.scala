package cz.payola.web.shared

import cz.payola.domain.entities.User
import s2js.compiler._

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

    def searchDataSources(needle: String) = {
    }
}
