package cz.payola.common.visual

import cz.payola.common.ValidationException

/**
 * RGB representation of colors used by visual plug-ins
 * @param red component of the color
 * @param green component of the color
 * @param blue component of the color
 */
class Color(val red: Int, val green: Int, val blue: Int, val alpha: Double = 1.0)
{
    /**
     * Creates new color inverted to this color.
     * @return inverted color
     */
    def inverse(): Color = {
        new Color(255 - red, 255 - green, 255 - blue, alpha)
    }

    /**
     * Converts this color to textual representation used by canvas context drawing techniques.
     * @return rgb("red","green","blue")
     */
    override def toString: String = {
        "rgba(%d,%d,%d,%0.1f)".format(red, green, blue, alpha)
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

    val Transparent = new Color(0, 0, 0, 0.0)

    /**
     * Converts color from rgb string
     * @param rgbString Properly formatted rgb string color representation
     * @return Represented color or None
     */
    def apply(rgbString: String): Option[Color] = {
        val rgbRegExp = "^rgba\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3},[0-9.]+\\)$"
        val colorValue = rgbString.toLowerCase.replace(" ", "")

        if (colorValue.matches(rgbRegExp)) {
            // Get RGB parts
            val parts = colorValue.replace("rgba(", "").replace(")", "").split(",")

            val r = parts(0).toInt
            val g = parts(1).toInt
            val b = parts(2).toInt
            val a = try {
                parts(3).toDouble
            }
            catch {
                case e: Exception => throw new ValidationException("color", e.getMessage())
            }

            // Validate
            if (r < 256 && g < 256 && b < 256 && a >= 0 && a <= 1) {
                Some(new Color(r, g, b, a))
            } else {
                None
            }
        } else {
            None
        }
    }
}
