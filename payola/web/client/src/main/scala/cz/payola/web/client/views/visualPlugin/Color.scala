package cz.payola.web.client.views.visualPlugin

// TODO byte would be better, but it isn't that simple, to make it work...
case class Color(red: Int, green: Int, blue: Int, alpha: Double = 1)
{
    def inverse(): Color = {
        Color(255 - red, 255 - green, 255 - blue, alpha)
    }

    override def toString: String = {
        // TODO use String.format when it's supported by the js runtime.
        "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + ")"
    }
}

object Color
{
    val Black = Color(0, 0, 0, 1)

    val Transparent = Color(0, 0, 0, 0)

    val White = Color(255, 255, 255, 1)
}
