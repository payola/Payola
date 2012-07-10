package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.EntityModelComponent
import cz.payola.domain.RdfStorageComponent

trait DataSourceModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val dataSourceModel = new EntityModel(dataSourceRepository)
    {
        def getPublic(count: Int, skip: Int = 0): Seq[DataSource] = {
            // TODO repository.getPublicDataSources(Some(PaginationInfo(skip, count)))
            Nil
        }
    }
}
