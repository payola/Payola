package cz.payola.web.shared

import s2js.compiler.async
import cz.payola.domain.entities.plugins.compiler.PluginCompiler
import cz.payola.domain.entities.plugins.PluginClassLoader

@remote object PluginManager
{
    @async def uploadPlugin(pluginCode: String)(successCallback: (String => Unit))(failCallback: (Throwable => Unit)) {
        // Try to compile code

        val libDirectory = new java.io.File("lib")
        val pluginClassDirectory = new java.io.File("plugins")
        if (!pluginClassDirectory.exists()){
            pluginClassDirectory.mkdir()
        }

        val compiler = new PluginCompiler(libDirectory, pluginClassDirectory)
        try {
            val className = compiler.compile(pluginCode)
            val loader = new PluginClassLoader(pluginClassDirectory, getClass.getClassLoader)
            val plugin = loader.getPlugin(className)

            if (Payola.model.pluginModel.getByName(plugin.name).isDefined) {
                failCallback(new Exception("Plugin with this name already exists!"))
            }else{
                Payola.model.pluginModel.persist(plugin)
                successCallback("Plugin saved.")
            }
        }catch {
            case e: Exception => {
                println(e)
                failCallback(new Exception("Code couldn't be compiled or loaded. \n\nDetails: " + e.getMessage))
            }
        }





    }


}
