package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.EntityModelComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.privileges.AccessDataSourcePrivilege

trait DataSourceModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val dataSourceModel = new ShareableEntityModel[DataSource](dataSourceRepository, classOf[AccessDataSourcePrivilege])
    {
        def create : DataSource = {
            //TODO
            getById("").get
        }
    }
}
