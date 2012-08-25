package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.EntityModelComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.plugins.ParameterValue

trait DataSourceModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PrivilegeModelComponent =>

    lazy val dataSourceModel = new ShareableEntityModel(dataSourceRepository, classOf[DataSource])
    {
        def persistParameterValue(parameterValue: ParameterValue[_]) {
            dataSourceRepository.persistParameterValue(parameterValue)
        }
    }
}
