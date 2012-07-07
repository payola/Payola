package cz.payola.web.shared

import com.typesafe.config._

@remote private[shared] class Settings(config: Config)
{
    val virtuosoServer = config.getString("virtuoso.server")

    val virtuosoEndpointPort = config.getInt("virtuoso.endpoint.port")

    val virtuosoEndpointSsl = config.getBoolean("virtuoso.endpoint.ssl")

    val virtuosoSqlPort = config.getInt("virtuoso.sql.port")

    val virtuosoSqlUser = config.getString("virtuoso.sql.user")

    val virtuosoSqlPassword = config.getString("virtuoso.sql.password")

    val databaseLocation = config.getString("database.location")

    val databaseUser = config.getString("database.user")

    val databasePassword = config.getString("database.password")
}
