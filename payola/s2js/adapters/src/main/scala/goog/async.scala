package s2js.adapters.goog.async

class ConditionalDelay(listener: () => Boolean)
{
    def isActive() = false

    def isDone() = false

    def onFailure() {}

    def onSuccess() {}

    def start(interval: Int = 0, timeout: Int = 0) {}

    def stop() {}
}

class Delay(listener: () => Unit, interval: Int = 0)
{
    def fire() {}

    def fireIfActive() {}

    def isActive() = false

    def start(interval: Int = 0) {}

    def stop() {}
}

class Throttle(listener: () => Unit, interval: Int = 0)
{
    def fire() {}

    def pause() {}

    def resume() {}

    def stop() {}
}
