package cz.payola.web.shared

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.virtuoso.VirtuosoStorage
import cz.payola.model.ModelComponent

@remote object Payola
{
    // TODO use a Configuration based on some config. file
    private[shared] lazy val configuration = new Configuration

    lazy val model: ModelComponent = new ModelComponent with SquerylDataContextComponent with RdfStorageComponent
    {
        lazy val schema = new Schema(
            configuration.databaseLocation,
            configuration.databaseUser,
            configuration.databasePassword
        )

        lazy val rdfStorage = new VirtuosoStorage(
            configuration.virtuosoServer,
            configuration.virtuosoEndpointPort,
            configuration.virtuosoEndpointSsl,
            configuration.virtuosoSqlPort,
            configuration.virtuosoSqlUser,
            configuration.virtuosoSqlPassword
        )
    }
}
