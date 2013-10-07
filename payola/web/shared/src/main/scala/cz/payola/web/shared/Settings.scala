package cz.payola.web.shared

import com.typesafe.config._
import s2js.compiler.remote

@remote private[shared] class Settings(config: Config)
{
    val virtuosoServer = config.getString("virtuoso.server")

    val virtuosoEndpointPort = config.getInt("virtuoso.endpoint.port")

    val virtuosoEndpointSsl = config.getBoolean("virtuoso.endpoint.ssl")

    val virtuosoSqlPort = config.getInt("virtuoso.sql.port")

    val virtuosoSqlUser = config.getString("virtuoso.sql.user")

    val virtuosoSqlPassword = config.getString("virtuoso.sql.password")

    val maxStoredAnalyses = config.getInt("virtuoso.astore.capacity.total")

    val maxStoredAnalysesPerUser = config.getInt("virtuoso.astore.capacity.peruser")

    val databaseLocation = config.getString("database.location")

    val databaseUser = config.getString("database.user")

    val databasePassword = config.getString("database.password")

    val adminEmail = config.getString("admin.email")

    val websiteURL = config.getString("web.url")

    val websiteNoReplyEmail = config.getString("web.email.noreply")

    val smtpServer = config.getString("mail.smtp.server")

    val smtpPort = config.getInt("mail.smtp.port")

    val smtpUsername = config.getString("mail.smtp.user")

    val smtpPassword = config.getString("mail.smtp.password")

    val libDirectory = new java.io.File(config.getString("lib.directory"))

    val pluginDirectory = new java.io.File(config.getString("plugin.directory"))
}
