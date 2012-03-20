package cz.payola.data.rdf

import actors.{TIMEOUT, Actor}
import messages.TimeoutMessage

class Timer(timeout: Long, executor: QueryExecutor) extends Actor
{
    def act() {
        reactWithin(timeout) {
            case TIMEOUT => {
                executor ! TimeoutMessage
            }
            case _ => // NOOP, any message received during the measured time means stopping of the Timer.
        }
    }
}
