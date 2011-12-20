package cz.payola.web.client.graph

// TODO byte would be better, but it isn't that simple, to make it work...
class Color(val red: Int, val green: Int, val blue: Int, val alpha: Double = 1) {
    override def toString: String = {
        // TODO use String.format when it's supported by the js runtime.
        "rgb(" + red + ", " + green + ", " +  blue + ", " + alpha + ")";
    }
}
