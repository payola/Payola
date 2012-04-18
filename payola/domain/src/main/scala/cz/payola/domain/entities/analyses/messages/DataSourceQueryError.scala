package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.entities.DataSource

case class DataSourceQueryError(dataSource: DataSource, throwable: Throwable) extends DataSourceQueryResult
