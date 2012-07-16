package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.privileges.UsePluginPrivilege
import cz.payola.domain.PluginCompilerComponent

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PluginCompilerComponent =>

    lazy val pluginModel = new ShareableEntityModel[Plugin](
        pluginRepository,
        classOf[UsePluginPrivilege],
        (user: User) => user.ownedPlugins)
    {
        def createPluginFromSource(source: String, user: User): Plugin = {
            val compiler = self.pluginCompiler
            val className = compiler.compile(source)
            val loader = self.pluginClassLoader
            val plugin = loader.instantiatePlugin(className)

            if (getByName(plugin.name).isDefined) {
                throw new Exception("Plugin with this name already exists!")
            }else{
                plugin.owner = Some(user)
                user.addOwnedPlugin(plugin)
                persist(plugin)
            }

            plugin
        }

        def getByName(name: String): Option[Plugin] = pluginRepository.getByName(name)
    }


}
