package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.entities.DataSource

abstract class DataSourceQueryResult

case class DataSourceQuerySuccess(dataSource: DataSource, data: String) extends DataSourceQueryResult

case class DataSourceQueryError(dataSource: DataSource, throwable: Throwable) extends DataSourceQueryResult
