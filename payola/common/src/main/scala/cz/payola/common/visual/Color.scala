package cz.payola.common.visual

import scala.Int
import java.lang.String
import s2js.compiler.javascript

/**
 * RGB representation of colors used by visual plug-ins
 * @param red component of the color
 * @param green component of the color
 * @param blue component of the color
 */
class Color(val red: Int, val green: Int, val blue: Int)
{
    /**
     * Creates new color inverted to this color.
     * @return inverted color
     */
    def inverse(): Color = {
        new Color(255 - red, 255 - green, 255 - blue)
    }

    /**
     * Converts this color to textual representation used by canvas context drawing techniques.
     * @return rgb("red","green","blue")
     */
    override def toString: String = {
        // TODO use String.format when it's supported by the js runtime.
        "rgb(" + red + "," + green + "," + blue + ")"
    }
}

/**
 * Few useful basic colors.
 */
object Color
{
    val Black = new Color(0, 0, 0)

    val White = new Color(255, 255, 255)

    val Red = new Color(255, 0, 0)

    val Green = new Color(0, 255, 0)

    val Blue = new Color(0, 0, 255)

    val Transparent = new Color(0, 0, 0)

    /**
     * Converts color from rgb string
     * @param rgbString Properly formatted rgb string color representation
     * @return Represented color or None
     */
    def apply(rgbString: String): Option[Color] = {
        val rgbRegExp = "^rgb\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3}\\)$"
        val colorValue = rgbString.toLowerCase.replace(" ", "")

        if (colorValue.matches(rgbRegExp)) {
            // Get RGB parts
            val parts = colorValue.replace("rgb(", "").replace(")", "").split(",")

            val r = parts(0).toInt
            val g = parts(1).toInt
            val b = parts(2).toInt

            // Validate
            if (r < 256 && g < 256 && b < 256) {
                Some(new Color(r, g, b))
            } else {
                None
            }
        } else {
            None
        }
    }
}
