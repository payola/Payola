package cz.payola.domain.actors

import actors.{TIMEOUT, Actor}

/**
  * An actor that sends a timeout message to the timeoutReceiver after specified number of milliseconds.
  * @param timeout The timeout in milliseconds.
  * @param timeoutReceiver The receiver of the timeout message.
  */
class Timer(private val timeout: Option[Long], private val timeoutReceiver: Actor) extends Actor
{
    def this(timeout: Long, timeoutReceiver: Actor) = this(Some(timeout), timeoutReceiver)

    def act() {
        if (timeout.isDefined) {
            reactWithin(timeout.get) {
                case TIMEOUT => {
                    timeoutReceiver ! TIMEOUT
                }
                case _ =>
            }
        } else {
            react {
                case _ =>
            }
        }
    }
}
