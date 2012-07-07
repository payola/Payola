package cz.payola.web.shared

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.virtuoso.VirtuosoStorage
import cz.payola.model.ModelComponent
import com.typesafe.config.ConfigFactory

@remote object Payola
{
    private[shared] lazy val settings = new Settings(ConfigFactory.load("payola"))

    lazy val model: ModelComponent = new ModelComponent with SquerylDataContextComponent with RdfStorageComponent
    {
        lazy val schema = new Schema(
            settings.databaseLocation,
            settings.databaseUser,
            settings.databasePassword
        )

        lazy val rdfStorage = new VirtuosoStorage(
            settings.virtuosoServer,
            settings.virtuosoEndpointPort,
            settings.virtuosoEndpointSsl,
            settings.virtuosoSqlPort,
            settings.virtuosoSqlUser,
            settings.virtuosoSqlPassword
        )
    }
}
