package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.web.shared._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.common.ValidationException

@remote @secured object PluginManager
    extends ShareableEntityManager[Plugin, cz.payola.common.entities.Plugin](Payola.model.pluginModel)
{

    def approvePluginByUser(className: String, owner: User, admin: User) = {
        if (admin.email == Payola.settings.adminEmail) {
            val result = Some(Payola.model.pluginModel.approvePluginWithClassName(className, owner))

            val email = new Email("Plugin Approved", "Your plugin (%s) has been approved.".format(result.get.name), "no-reply@payola.cz", List(owner.email))
            email.send()

            result
        }else{
            None
        }
    }

    def rejectPlugin(className: String, owner: User, admin: User): Boolean = {
        // Currently, only send an email to the owner. Maybe in the future delete the plugin?
        if (admin.email == Payola.settings.adminEmail) {
            val email = new Email("Plugin Rejected", "Sorry, but your plugin has been rejected.", "no-reply@payola.cz", List(owner.email))
            email.send()
            true
        }else{
            false
        }
    }

    @secured @async def getAccessibleDataFetchers(user: Option[User] = null)
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
        val approveLink = "http://%s/plugin/approve/%s/%s".format(Payola.settings.websiteURL, className, user.id)
        val declineLink = "http://%s/plugin/reject/%s/%s".format(Payola.settings.websiteURL, className, user.id)

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

        val email = new Email("Plugin Approval", emailText, "no-reply@payola.cz", List(Payola.settings.adminEmail))
        email.send()

        successCallback()
    }
}
