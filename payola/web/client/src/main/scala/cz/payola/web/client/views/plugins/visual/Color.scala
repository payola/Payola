package cz.payola.web.client.views.plugins.visual

/**
  * RGBA representation of colors used by visual plug-ins
  * @param red component of the color
  * @param green component of the color
  * @param blue component of the color
  * @param alpha component of the color ("see through attribute")
  */
case class Color(red: Int, green: Int, blue: Int, alpha: Double = 1)
{
    /**
      * Creates new color inverted to this color.
      * @return inverted color
      */
    def inverse(): Color = {
        Color(255 - red, 255 - green, 255 - blue, alpha)
    }

    /**
      * Converts this color to textual representation used by canvas context drawing techniques.
      * @return rgba("red", "green", "blue", "alpha")
      */
    override def toString: String = {
        // TODO use String.format when it's supported by the js runtime.
        "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + ")"
    }
}

/**
  * Few useful basic colors.
  */
object Color
{
    val Black = Color(0, 0, 0, 1)

    val Transparent = Color(0, 0, 0, 0)

    val White = Color(255, 255, 255, 1)
}
