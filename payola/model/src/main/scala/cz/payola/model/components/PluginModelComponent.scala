package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model._
import cz.payola.domain.PluginCompilerComponent

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PluginCompilerComponent with PrivilegeModelComponent =>

    lazy val pluginModel = new ShareableEntityModel(pluginRepository, classOf[Plugin])
    {
        def createPluginFromSource(source: String, user: User): Plugin = {
            val compiler = self.pluginCompiler
            val className = compiler.compile(source)
            val loader = self.pluginClassLoader

            val plugin = loader.instantiatePlugin(className)
            plugin.owner = Some(user)
            persist(plugin)
            plugin
        }

        def getByName(name: String): Option[Plugin] = pluginRepository.getByName(name)
    }
}
