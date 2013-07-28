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

    @async def searchAccessibleAnalyses(term: String, user: Option[User] = null)(successCallback: (Seq[Analysis] => Unit))
        (failCallback: (Throwable => Unit)) {
        val analyses = Payola.model.analysisModel.getAccessibleToUser(user).filter(_.name.contains(term))
        successCallback(analyses)
    }

    @async def getAnalysisById(analysisId: String, user: Option[User] = None)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {

        val analysis = Payola.model.analysisModel.getAccessibleToUserById(user,analysisId).getOrElse{
            throw new Exception("Analysis not found.")
        }

        successCallback(analysis)
    }

    /**
     * Clone analysis button functionality
     * @param analysisId Analysis to clone
     * @param user current user
     * @param successCallback
     * @param failCallback
     * @return
     */
    @async def cloneAnalysis(analysisId: String, user: Option[User] = None)(successCallback: (Analysis => Unit))
        (failCallback: (Throwable => Unit)) {
        successCallback(Payola.model.analysisModel.clone(analysisId, user))
    }
}
