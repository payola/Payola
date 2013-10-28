package s2js.adapters.js.sigma

trait Node {
    var x: Double = 0
    var y: Double = 0
    var size: Int = 0
    var color: String = "red"
    var value: Any = null
    var label: String = ""
    //val attr: List[(String, String)] = null //hidden for javascript only
}
