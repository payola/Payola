package cz.payola.web.shared

import s2js.compiler._
import cz.payola.domain.entities.plugins.compiler.PluginCompiler
import cz.payola.domain.entities.plugins.PluginClassLoader
import cz.payola.domain.entities.User

@remote object PluginManager
{
    @async @secured def uploadPlugin(pluginCode: String, user: User = null)(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {
        // Sanity check
        assert(user != null, "Not logged in, or some other error")

        // Try to compile code
        try {
            val plugin = Payola.model.pluginModel.createPluginFromSource(pluginCode, user)
            if (plugin != null) {
                successCallback("Plugin saved.")
            }
        }catch{
            case e: Exception => {
                e.printStackTrace()
                failCallback(new Exception("Couldn't save plugin.\n\nDetails: " + e.getMessage))
            }
        }
    }


}
