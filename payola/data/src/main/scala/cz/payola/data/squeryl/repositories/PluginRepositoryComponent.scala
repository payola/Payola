package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data._
import cz.payola.data.squeryl.entities.PluginDbRepresentation
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.entities.plugins.Parameter
import scala.Some
import cz.payola.data.squeryl._

trait PluginRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val pluginRepository = new PluginRepository[Plugin]
    {
        private val representationRepository = new TableRepository[PluginDbRepresentation](schema.plugins,
            PluginDbRepresentation)

        def getById(id: String): Option[Plugin] = representationRepository.getById(id).map(_.toPlugin)

        def removeById(id: String): Boolean = representationRepository.removeById(id)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = {
            representationRepository.getAll(pagination).map(_.toPlugin)
        }

        def persist(entity: AnyRef): Plugin = {
            entity match {
                case plugin: Plugin => {
                    // Persist the plugin and its parameters.
                    val representation = representationRepository.persist(entity)
                    plugin.parameters.map(parameter => representation.associateParameter(Parameter(parameter)))
                    plugin
                }
                case _ => throw new DataException("Couldn't convert the entity to a plugin.")
            }
        }

        def getByName(pluginName: String): Option[Plugin] = {
            representationRepository.evaluateSingleResultQuery(representationRepository.table.where(p => p.name === pluginName)).map(_.toPlugin)
        }
    }
}
