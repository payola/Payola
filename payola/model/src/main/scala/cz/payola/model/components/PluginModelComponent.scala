package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model._
import cz.payola.domain.PluginCompilerComponent
import cz.payola.domain.entities.plugins.compiler.PluginInfo
import cz.payola.common.ValidationException

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PluginCompilerComponent with PrivilegeModelComponent =>

    lazy val pluginModel = new ShareableEntityModel(pluginRepository, classOf[Plugin])
    {
        /** Approves the plugin by user.
         *
         * @param className Class name of the plugin.
         * @param user User whose plugin it is.
         * @return A new instance of the plugin.
         */
        def approvePluginWithClassName(className: String, user: User): Plugin = {
           val loader = self.pluginClassLoader

            val plugin = loader.instantiatePlugin(pluginInfo.className)
            plugin.owner = Some(user)
            persist(plugin)
            plugin
        }

        /** Compiles a plugin and stores it in the plugins directory. It is not added to
         * the database.
         *
         * @param source Source of the plugin.
         * @return Returns the class name.
         */
        def compilePluginFromSource(source: String): PluginInfo = {
            val compiler = self.pluginCompiler
            val classInfo = compiler.compile(source)

            if (this.getByName(classInfo.name).isDefined){
                throw new ValidationException("A plugin with this name already exists.")
            }

            classInfo.className
        }

        def getByName(name: String): Option[Plugin] = pluginRepository.getByName(name)
    }
}
