package cz.payola.data

import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.analyses.PluginInstanceBinding

trait DataContextComponent
{
    val userDAO: DAO[User]

    val groupDAO: DAO[Group]

    val analysisDAO: DAO[Analysis]

    val pluginInstanceDAO: DAO[PluginInstance]

    val pluginInstanceBindingDAO: DAO[PluginInstanceBinding]

    val dataSourceDAO: DAO[DataSource]

    trait DAO[+A]
    {
        def getById(id: String): Option[A]

        def removeById(id: String): Boolean

        def getAll(pagination: Option[PaginationInfo] = None): Seq[A]
    }
}
