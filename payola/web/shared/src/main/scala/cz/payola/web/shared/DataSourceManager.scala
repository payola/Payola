package cz.payola.web.shared

import s2js.compiler.secured
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import scala.Some
import scala.collection.mutable.ListBuffer

@remote object DataSourceManager
{
    @secured def getAvailableDataFetchers(user: User = null): Seq[Plugin] = {
        Payola.model.pluginModel.getAccessibleToUser(Some(user)).filter(p => p.isInstanceOf[DataFetcher])
    }

    @secured def dataSourceExistsWithName(name: String, user: User = null): Boolean = {
        Payola.model.dataSourceModel.getAll.exists(_.name == name)
    }

}
