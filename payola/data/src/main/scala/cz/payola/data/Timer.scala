package cz.payola.data

import actors.Actor._
import actors.{TIMEOUT, Actor}
import sparql.messages.TimeoutMessage

class Timer(timeout : Long, receiver : Actor) extends Actor{
    def act() = {
        receiveWithin(timeout) {
            case TIMEOUT => {
                receiver ! TimeoutMessage
                exit()
            }

            case msg => {
                println("Timer: (invalid)" + msg)
            }
        }
    }
}