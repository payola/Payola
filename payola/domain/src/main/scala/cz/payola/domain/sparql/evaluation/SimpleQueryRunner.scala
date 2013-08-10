package cz.payola.domain.sparql.evaluation

import actors.Actor
import cz.payola.domain.entities.plugins.DataSource

class SimpleQueryRunner(query: String, dataSource: DataSource,
    private val parentRunner: SimpleTimeoutQueryRunner) extends Actor {

    def act() {
        try{
            val resultGraph = dataSource.executeQuery(query)
            parentRunner ! SuccessResult(Some(resultGraph))

        } catch {
            case e: Throwable => parentRunner ! ErrorResult(e)
            case _ => parentRunner ! ErrorResult
        }
    }
}
