package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model._
import cz.payola.domain._
import cz.payola.domain.entities.plugins.compiler._
import cz.payola.common.ValidationException
import plugins.concrete._
import plugins.Parameter
import plugins.parameters._
import cz.payola.common.rdf.DataCubeDataStructureDefinition
import scala.Some

trait PluginModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PluginCompilerComponent with PrivilegeModelComponent =>
    lazy val pluginModel = new ShareableEntityModel(pluginRepository, classOf[Plugin])
    {
        /**
         * Compiles a plugin and stores it in the plugins directory. It is not added to the database.
         * @param source Source of the plugin.
         * @return The plugin class name.
         */
        def compilePluginFromSource(source: String): String = {
            try {
                val pluginInfo = pluginCompiler.compile(source)
                if (getByName(pluginInfo.name).isDefined) {
                    throw new ValidationException("A plugin with this name already exists.")
                }
                pluginInfo.className
            } catch {
                case e: PluginCompilationException => throw new ValidationException(e.message)
            }
        }

        /**
         * Approves the plugin uploaded by the specified user.
         * @param className Class name of the plugin.
         * @param user User whose plugin it is.
         * @return A new instance of the plugin.
         */
        def approvePluginWithClassName(className: String, user: User): Plugin = {
            val loader = self.pluginClassLoader
            val plugin = loader.instantiatePlugin(className)
            plugin.owner = Some(user)
            persist(plugin)
            plugin
        }

        def createDataCubeInstance(dataCubeDataStructure: DataCubeDataStructureDefinition, owner: User): Plugin = {
            pluginRepository.getByName(dataCubeDataStructure.uri).getOrElse {
                val plugin = new DataCube(dataCubeDataStructure)
                plugin.owner = None
                plugin.isPublic = true
                pluginRepository.persist(plugin)
                plugin
            }
        }

        def cloneParameter(parameter: Parameter[_]): Parameter[_] = {
            parameter match {
                case x: StringParameter => new
                        StringParameter(x.name+"$"+x.id, x.defaultValue, x.isMultiline, x.isPattern, x.isMultiline)
                case x: BooleanParameter => new BooleanParameter(x.name+"$"+x.id, x.defaultValue)
                case x: FloatParameter => new FloatParameter(x.name+"$"+x.id, x.defaultValue)
                case x: IntParameter => new IntParameter(x.name+"$"+x.id, x.defaultValue)
                case _ => throw new Exception
            }
        }

        def createAnalysisInstance(paramIds: Seq[String], analysis: Analysis, owner: Option[User]): Plugin = {
            val parameters = List(
                new StringParameter("Analysis ID", analysis.id, false, false, false)) ++
                analysis.pluginInstances
                    .map(_.plugin)
                    .flatMap(_.parameters)
                    .filter {
                    p => paramIds.exists(_ == p.id)
                }.map(cloneParameter(_))

            val plugin = new AnalysisPlugin(analysis, parameters.toSeq)
            plugin.owner = owner
            plugin.isPublic = false
            pluginRepository.persist(plugin)
            plugin
        }
    }
}
