package s2js.adapters.goog

import s2js.adapters.goog.events._

class Timer(interval: Int) extends EventTarget
{
    val enabled = false

    def getInterval(): Int = 0

    def setInterval(interval: Int) {}

    def dispatchTick() {}

    def start() {}

    def stop() {}
}

object Timer
{
    val intervalScale = 0.8
    val TICK = "tick"

    def callOnce(listener: () => Unit, delay: Int = 0, handler: Any = null): Int = 0

    def clear(timerId: Int) {}
}
