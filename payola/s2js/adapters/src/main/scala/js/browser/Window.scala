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

    def setTimeout(fn: () => Unit, milliseconds: Int) {}

    object location
    {
        var href = ""
    }

    object history
    {
        // TODO
    }

}
