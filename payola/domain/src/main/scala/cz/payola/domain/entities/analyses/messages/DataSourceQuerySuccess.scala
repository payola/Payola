package cz.payola.domain.entities.analyses.messages

import cz.payola.domain.entities.DataSource

case class DataSourceQuerySuccess(dataSource: DataSource, data: String) extends DataSourceQueryResult
