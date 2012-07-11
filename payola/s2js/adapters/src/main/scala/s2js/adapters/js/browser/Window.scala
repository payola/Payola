package s2js.adapters.js.browser

class Window
{
    val sessionStorage: Storage = null

    val localStorage: Storage = null

    val innerWidth: Double = 0

    val innerHeight: Double = 0

    def get(key: String) = ""

    def focus() {}

    def alert(s: Any) {}

    def setTimeout(fn: () => Unit, milliseconds: Int): Int = 0

    def clearTimeout(timeoutId: Int): Int = 0

    def setInterval(fn: () => Unit, milliseconds: Int): Int = 0
    
    def clearInterval(intervalId: Int): Int = 0

    object location
    {
        var href = ""
    }

    object history
    {
        // TODO
    }

    /**
      * window resizing event
      */
    var onresize: (Event => Boolean) = (event: Event) => false
}
