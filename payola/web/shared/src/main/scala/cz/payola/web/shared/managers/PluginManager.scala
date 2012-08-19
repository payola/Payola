package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.web.shared.Payola
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import s2js.runtime.shared.rpc.RpcException

@remote @secured object PluginManager
    extends ShareableEntityManager[Plugin, cz.payola.common.entities.Plugin](Payola.model.pluginModel)
{
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
    @async def uploadPlugin(pluginCode: String, user: User = null)(successCallback: (String => Unit))
        (failCallback: (Throwable => Unit)) {

        // Try to compile code
        // TODO use validation exceptions.
        try {
            Payola.model.pluginModel.createPluginFromSource(pluginCode, user)
            successCallback("Plugin saved.")
        } catch {
            case e: Exception => {
                failCallback(new RpcException("Couldn't save plugin.\n\nDetails: " + e.getMessage))
            }
        }
    }
}
