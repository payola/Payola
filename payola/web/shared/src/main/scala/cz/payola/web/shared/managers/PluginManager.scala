package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.web.shared._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import scala.Some
import cz.payola.web.shared.Email

@remote
@secured object PluginManager
    extends ShareableEntityManager[Plugin, cz.payola.common.entities.Plugin](Payola.model.pluginModel)
{
    @async def getAccessibleDataFetchers(user: Option[User] = null)
        (successCallback: Seq[cz.payola.common.entities.Plugin] => Unit)
        (errorCallback: Throwable => Unit) {
        successCallback(model.getAccessibleToUser(user).filter(_.isInstanceOf[DataFetcher]))
    }

    /** Attempts to create a new plugin from pluginCode.
      *
      * @throws Exception when an error occurs (compilation, already-existing name, ...).
      *                   Details in exception's message.
      * @param pluginCode Code of the plugin.
      * @param user User.
      * @param successCallback Success callback.
      * @param failCallback Fail callback.
      */
    @async def uploadPlugin(pluginCode: String, user: User = null)
        (successCallback: (() => Unit))
        (failCallback: (Throwable => Unit)) {
        val className = Payola.model.pluginModel.compilePluginFromSource(pluginCode)
        val approveLink = "%s/plugin/approve/%s/%s".format(Payola.settings.websiteURL, className, user.id)
        val declineLink = "%s/plugin/reject/%s/%s".format(Payola.settings.websiteURL, className, user.id)

        val emailText =
            """Dear admin,
              |
              |please, review the following plugin code and decide whether to approve the plugin
              |for usage or to decline it.
              |
              |%s
              |
              |Approve: %s
              |Decline: %s
              |
            """.stripMargin.format(pluginCode, approveLink, declineLink)

        Email(
            "Plugin Approval",
            emailText,
            Payola.settings.websiteNoReplyEmail,
            List(Payola.settings.adminEmail)
        ).send()

        successCallback()
    }

    def approvePluginByUser(className: String, owner: User, admin: User) = {
        if (admin.email == Payola.settings.adminEmail) {
            val plugin = Some(Payola.model.pluginModel.approvePluginWithClassName(className, owner))
            Email(
                "Plugin Approved",
                "Your plugin (%s) has been approved.".format(plugin.get.name),
                Payola.settings.websiteNoReplyEmail,
                List(owner.email)
            ).send()

            plugin
        } else {
            None
        }
    }

    def rejectPlugin(className: String, owner: User, admin: User): Boolean = {
        // Currently, only send an email to the owner. Maybe in the future delete the plugin?
        val isAdmin = admin.email == Payola.settings.adminEmail
        if (isAdmin) {
            Email(
                "Plugin Rejected",
                "Sorry, but your plugin has been rejected.",
                Payola.settings.websiteNoReplyEmail,
                List(owner.email)
            ).send()
        }
        isAdmin
    }

    /**
     * DataCube plugin from DSD, remote proxy
     * @param vocabularyURI URI with the vocabulary
     * @param dataStructureURI DSD identifier
     * @param owner plugin owner [deprecated]
     * @param successCallback
     * @param failCallback
     * @return
     * @author Jiri Helmich
     */
    @async def createDataCubeInstance(vocabularyURI: String, dataStructureURI: String, owner: User = null)
        (successCallback: (cz.payola.common.entities.Plugin => Unit))
        (failCallback: (Throwable => Unit)) {

        Payola.model.dataCubeModel.loadVocabulary(vocabularyURI)
            .dataStructureDefinitions
            .find(_.uri == dataStructureURI).map { d =>
                val plugin = Payola.model.pluginModel.createDataCubeInstance(d, owner)
                successCallback(plugin)
            }.getOrElse {
                failCallback(new Exception)
            }
    }

    @async def createAnalysisInstance(paramIds: Seq[String], analysisId: String, user: Option[User] = None)
        (successCallback: (cz.payola.common.entities.Plugin => Unit))
        (failCallback: (Throwable => Unit)) {

        Payola.model.analysisModel.getAccessibleToUser(user).find(_.id == analysisId).map{ analysis =>
            val plugin = Payola.model.pluginModel.createAnalysisInstance(paramIds, analysis, user)
            successCallback(plugin)
        }.getOrElse{ failCallback(new Exception) }

    }
}
