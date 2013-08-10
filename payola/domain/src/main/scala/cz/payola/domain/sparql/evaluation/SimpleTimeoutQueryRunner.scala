package cz.payola.domain.sparql.evaluation

import cz.payola.domain.actors.Timer
import actors.{TIMEOUT, Actor}
import cz.payola.domain.entities.plugins.DataSource

/**
 * An actor that launches and measures running time of a sparql query. It creates another actor that
 * performs the query (SimpleQueryRunner), a Timer actor and launches both of them and waits for
 * a result of the query or a timeout.
 * @param query What query to perform.
 * @param dataSource On what datasource to perform the query.
 * @param timeout How long to wait for the results.
 */
class SimpleTimeoutQueryRunner(query: String, dataSource: DataSource,
    private val timeout: Option[Long]) extends Actor {

    private val timer = new Timer(timeout, this)
    private var result: Option[QueryResult] = None
    private var actorChild: Option[SimpleQueryRunner] = None
    def act() {
        timer.start()
        actorChild = Some(new SimpleQueryRunner(query, dataSource, this))
        actorChild.get.start()
        loop {
            react {
                case ErrorResult => {
                    finishEvaluation(ErrorResult)
                }
                case SuccessResult(languagesGraph) => {
                    result = Some(SuccessResult(languagesGraph))
                }
                case TIMEOUT => {
                    finishEvaluation(TimeoutResult)
                }
                case control: QueryRunnerControl => {
                    processControlMessage(control)
                }
            }
        }
    }

    private def processControlMessage(message: QueryRunnerControl) {
        message match {
            case GetResult => {
                reply(result)
            }
            case Stop if result.isEmpty => finishEvaluation(StoppedResult)
            case Terminate => {
                terminateChild()
                exit()
            }
        }
    }

    private def finishEvaluation(queryResult: QueryResult) {
        terminateChild()
        result = Some(queryResult)
        loop {
            react {
                case control: QueryRunnerControl => {
                    processControlMessage(control)
                }
            }
        }
    }

    private def terminateChild() {
        timer ! None
        actorChild.foreach{_ ! None}
    }

    /**
     * End this actor.
     */
    def finish {
        this !? Terminate
    }

    /**
     * Fetch result of the query.
     * @return current result of the query (the query is still running and the timeout has not orruced if None)
     */
    def getResult: Option[QueryResult] = {
        (this !? GetResult).asInstanceOf[Option[QueryResult]]
    }

    /**
     * Is true only if some result of the query evaluation was reached (even if it is a timeout or an error).
     * @return false if query is still running and timeout has not occured
     */
    def isFinished = getResult.isDefined
}