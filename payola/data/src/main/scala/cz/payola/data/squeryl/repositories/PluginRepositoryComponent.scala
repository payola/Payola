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

    lazy val pluginRepository = new PluginRepository
    {
        private val representationRepository = new LazyTableRepository[PluginDbRepresentation](schema.plugins,
            PluginDbRepresentation)

        def getByIds(ids: Seq[String]): Seq[Plugin] = {
            representationRepository.getByIds(ids).map(_.toPlugin)
        }

        def removeById(id: String): Boolean = {
            representationRepository.removeById(id)
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = {
            representationRepository.getAll(pagination).map(_.toPlugin)
        }

        def getAllPublic: Seq[Plugin] = {
            representationRepository.selectWhere(_.isPublic === true).map(_.toPlugin)
        }

        def getAllByOwnerId(ownerId: Option[String]): Seq[Plugin] = {
            representationRepository.selectWhere(_.ownerId === ownerId).map(_.toPlugin)
        }

        def getByName(name: String): Option[Plugin] = {
            representationRepository.selectOneWhere(_.name === name).map(_.toPlugin)
        }

        def persist(entity: AnyRef): Plugin = schema.wrapInTransaction {
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

        def getCount: Long = {
            representationRepository.getCount
        }
    }
}
