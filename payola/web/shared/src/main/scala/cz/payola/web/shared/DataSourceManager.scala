package cz.payola.web.shared

import s2js.compiler.secured
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import scala.Some

@remote object DataSourceManager
{
    /** Gets available data fetchers (plugins that are instances of DataFetcher class).
      *
      * @param user User.
      * @return Data fetchers accessible to the user.
      */
    @secured def getAvailableDataFetchers(user: User = null): Seq[Plugin] = {
        Payola.model.pluginModel.getAccessibleToUser(Some(user)).filter(p => p.isInstanceOf[DataFetcher])
    }

    /** Returns true if a data source with this name already exists.
      *
      * @param name Name of the potential data source.
      * @param user User.
      * @return True or false.
      */
    @secured def dataSourceExistsWithName(name: String, user: User = null): Boolean = {
        Payola.model.dataSourceModel.getAll().exists(_.name == name)
    }

}
