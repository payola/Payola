package cz.payola.data

import actors.Actor
import actors.Actor._

class Timer(receiver : Actor) extends Actor{
    def initialize() = {
        start();
    }

    def act() = {
        /*
        receive {
            case x : TimerMessage =>
                // Wait for given timeout (comes in seconds)
                Thread.sleep(x.timeout * 1000);

                // Send wake-up call
                receiver ! x;

            case _ : StopMessage =>
                exit();

            case msg =>
                println("Timer: (invalid)" + msg);
                receiver ! msg;
        }
        */
    }
}