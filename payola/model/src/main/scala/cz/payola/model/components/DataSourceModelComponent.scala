package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.EntityModelComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.privileges.AccessDataSourcePrivilege
import cz.payola.domain.entities.User

trait DataSourceModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PrivilegeModelComponent =>

    lazy val dataSourceModel = new ShareableEntityModel(dataSourceRepository, classOf[DataSource])
}
