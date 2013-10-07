package cz.payola.web.shared

import cz.payola.data.squeryl.SquerylDataContextComponent
import s2js.compiler.remote
import cz.payola.domain._
import cz.payola.data.virtuoso.VirtuosoStorage
import cz.payola.model.ModelComponent
import com.typesafe.config.ConfigFactory
import cz.payola.domain.entities.plugins.PluginClassLoader
import cz.payola.domain.entities.plugins.compiler.PluginCompiler

@remote object Payola
{
    lazy val settings = new Settings(ConfigFactory.load("payola"))

    lazy val model: ModelComponent = new ModelComponent
        with SquerylDataContextComponent
        with RdfStorageComponent
        with PluginCompilerComponent
    {

        override val maxStoredAnalyses: Long = settings.maxStoredAnalyses

        override val maxStoredAnalysesPerUser: Long = settings.maxStoredAnalysesPerUser

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

        lazy val pluginCompiler = new PluginCompiler(
            settings.libDirectory,
            settings.pluginDirectory
        )

        lazy val pluginClassLoader = new PluginClassLoader(
            settings.pluginDirectory,
            getClass.getClassLoader
        )
    }
}
