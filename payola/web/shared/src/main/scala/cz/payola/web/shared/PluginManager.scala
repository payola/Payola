package cz.payola.web.shared

import s2js.compiler._
import cz.payola.domain.entities.User

@remote object PluginManager
{
    /** Attempts to create a new plugin from pluginCode.
      *
      * @throws Exception when an error occurs (compilation, already-existing name, ...).
      *                   Details in exception's message.
      * @param pluginCode Code of the plugin.
      * @param user User.
      * @param successCallback Success callback.
      * @param failCallback Fail callback.
      */
    @async @secured def uploadPlugin(pluginCode: String, user: User = null)(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {
        // Sanity check
        assert(user != null, "Not logged in, or some other error")

        // Try to compile code
        try {
            Payola.model.pluginModel.createPluginFromSource(pluginCode, user)
            successCallback("Plugin saved.")
        }catch{
            case e: Exception => {
                e.printStackTrace()
                failCallback(new Exception("Couldn't save plugin.\n\nDetails: " + e.getMessage))
            }
        }
    }


}
