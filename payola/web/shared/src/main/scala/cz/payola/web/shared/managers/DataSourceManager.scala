package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.User

@remote @secured object DataSourceManager
    extends ShareableEntityManager[DataSource, cz.payola.common.entities.plugins.DataSource](
        Payola.model.dataSourceModel)
{
    /** Returns true if a data source with this name already exists.
      *
      * @param name Name of the potential data source.
      * @param user User.
      * @return True or false.
      */
    def dataSourceExistsWithName(name: String, user: User = null): Boolean = {
        model.getAll().exists(_.name == name)
    }
}
