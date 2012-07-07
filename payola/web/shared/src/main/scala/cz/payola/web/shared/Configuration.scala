package cz.payola.web.shared

@remote private[shared] class Configuration
{
    val virtuosoServer = "localhost"

    val virtuosoEndpointPort = 8890

    val virtuosoEndpointSsl = false

    val virtuosoSqlPort = 1111

    val virtuosoSqlUser = "dba"

    val virtuosoSqlPassword = "dba"

    val databaseLocation = "jdbc:h2:tcp://localhost/~/h2/payola"

    val databaseUser = "sa"

    val databasePassword = ""
}
