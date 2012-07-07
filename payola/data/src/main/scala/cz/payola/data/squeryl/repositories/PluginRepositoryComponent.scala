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

    // TODO take special care of the PayolaStorage plugin instantiation.
    lazy val pluginRepository = new Repository[Plugin]
    {
        def getById(id: String): Option[Plugin] = None // TODO

        def removeById(id: String): Boolean = false // TODO

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = Nil // TODO

        /**
          * Returns [[cz.payola.domain.entities.Plugin]] by its name.
          *
          * @param pluginName - name of a plugin to search
          * @return Return Some([[cz.payola.domain.entities.Plugin]]) if found, None otherwise
          */
        def getByName(pluginName: String): Option[Plugin] = {
            /*TODO// Get plugin representation from DB
            val pluginDb: Option[PluginDbRepresentation] =
                evaluateSingleResultQuery(table.where(p => p.name === pluginName))

            if (pluginDb.isDefined) {
                Some(pluginDb.get.createPlugin())
            }
            else {
                // Not found
                None
            }*/
            None
        }

        /**
          * Inserts or updates [[cz.payola.domain.entities.Plugin]].
          *
          * @param p - plugin to insert or update
          * @return Returns persisted [[cz.payola.domain.entities.Plugin]]
          */
        def persist(entity: AnyRef): Plugin = {
            /*TODO val pluginDb = PluginDbRepresentation(p)

            // First persist plugin ...
            val result = super.persist(pluginDb)

            // ... then assign parameters
            p.parameters.map(par => pluginDb.associateParameter(Parameter(par)))

            result.createPlugin()*/
            null
        }
    }
}
