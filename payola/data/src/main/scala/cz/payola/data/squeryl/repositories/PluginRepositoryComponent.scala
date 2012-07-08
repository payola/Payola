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

    lazy val pluginRepository = new Repository[Plugin]
    {
        private val _repository = new TableRepository[PluginDbRepresentation](schema.plugins, PluginDbRepresentation)

        def getById(id: String): Option[Plugin] = _repository.getById(id).map(_.createPlugin())

        def removeById(id: String): Boolean = _repository.removeById(id)

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = {
            _repository.getAll(pagination).map(_.createPlugin())
        }

        def persist(entity: AnyRef): Plugin = {
            // First persist plugin ...
            val pluginDb = _repository.persist(entity)

            // ... then assign parameters ...
            entity.asInstanceOf[Plugin].parameters.map(par => pluginDb.associateParameter(Parameter(par)))

            // ... and return plugin
            entity.asInstanceOf[Plugin]
        }

        def getByName(pluginName: String): Option[Plugin] = {
            _repository.evaluateSingleResultQuery(_repository.table.where(p => p.name === pluginName)).map(_.createPlugin())
        }
    }
}
