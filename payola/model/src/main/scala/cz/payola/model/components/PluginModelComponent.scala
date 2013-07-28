package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities._
import cz.payola.model._
import cz.payola.domain._
import cz.payola.domain.entities.plugins.compiler._
import cz.payola.common.ValidationException
import plugins.concrete._
import cz.payola.domain.entities.plugins._
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

        /**
         * Creates a DataCube plugin based on DSD. Therefore a plugin in created in the process, if it does not exist.
         * @param dataCubeDataStructure DSD
         * @param owner Obsolete. The plugin is always public.
         * @return Plugin
         * @author Jiri Helmich
         */
        def createDataCubeInstance(dataCubeDataStructure: DataCubeDataStructureDefinition, owner: User): Plugin = {
            pluginRepository.getByName(dataCubeDataStructure.uri).getOrElse {
                val plugin = new DataCube(dataCubeDataStructure)
                plugin.owner = None
                plugin.isPublic = true
                pluginRepository.persist(plugin)
                plugin
            }
        }

        /**
         * Parameters cloning.
         * @param parameterValue Value to be cloned
         * @param name Parameter name
         * @return Cloned parameter
         * @author Jiri Helmich
         */
        def cloneParameter(parameterValue: ParameterValue[_], name: String): Parameter[_] = {
            val parameter = parameterValue.parameter

            parameter match {
                case x: StringParameter => new
                        StringParameter(name+"$"+parameterValue.id, x.defaultValue, x.isMultiline, x.isPattern, x.isMultiline)
                case x: BooleanParameter => new BooleanParameter(name+"$"+parameterValue.id, x.defaultValue)
                case x: FloatParameter => new FloatParameter(name+"$"+parameterValue.id, x.defaultValue)
                case x: IntParameter => new IntParameter(name+"$"+parameterValue.id, x.defaultValue)
                case _ => throw new Exception
            }
        }

        /**
         * @author Jiri Helmich
         */
        def createAnalysisInstance(paramValIds: Seq[String], analysis: Analysis, owner: Option[User]): Plugin = {
            def iterateParams: Seq[Parameter[_]] = {
                paramValIds.map(_.split(":~:")).map {
                    t => analysis.pluginInstances.flatMap(_.parameterValues).find(_.id == t(0))
                        .map(cloneParameter(_, t(1)))
                }.flatten
            }
            val parameters = List(
                new StringParameter("Analysis ID", analysis.id, false, false, false)) ++
                iterateParams

            val plugin = new AnalysisPlugin(analysis, parameters.toSeq)
            plugin.owner = owner
            plugin.isPublic = false
            pluginRepository.persist(plugin)
            plugin
        }
    }
}
