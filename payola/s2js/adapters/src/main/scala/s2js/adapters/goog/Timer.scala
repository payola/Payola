package s2js.adapters.goog

import s2js.adapters.goog.events.EventTarget

class Timer(interval: Int, timerObject: AnyRef = null) extends EventTarget
{
    val enabled: Boolean = false

    def dispatchTick() {}

    def getInterval: Int = 0

    def setInterval(interval: Int) {}

    def start() {}

    def stop() {}
}

object Timer
{
    val intervalScale = 0.8

    val TICK = "tick"

    def callOnce(listener: () => Unit, delay: Int = 0): Int = 0

    def clear(timerId: Int) {}
}
