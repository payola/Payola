package cz.payola.web.client.views.plugins.visual

import math.Ordering.String
import s2js.runtime.client.scala.collection.mutable.HashMap
import scala.Int
import java.lang.String

/**
  * RGBA representation of colors used by visual plug-ins
  * @param red component of the color
  * @param green component of the color
  * @param blue component of the color
  * @param alpha component of the color ("see through attribute")
  */
case class Color(var red: Int, var green: Int, var blue: Int, var alpha: Double = 1)
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

    def toHexString: String = {
        "#"+dec2hex(red)+dec2hex(green)+dec2hex(blue)+dec2hex((alpha*255).toInt)
    }

    private def dec2hex(n: Int) : String = {
        convertDecToHex(n/16)+convertDecToHex(n%16)
    }

    private def convertDecToHex(n: Int) : String = {
        n match {
            case 0 => "0"
            case 1 => "1"
            case 2 => "2"
            case 3 => "3"
            case 4 => "4"
            case 5 => "5"
            case 6 => "6"
            case 7 => "7"
            case 8 => "8"
            case 9 => "9"
            case 10 => "a"
            case 11 => "b"
            case 12 => "c"
            case 13 => "d"
            case 14 => "e"
            case 15 => "f"
            case _ => "0"
        }
    }
}

/**
  * Few useful basic colors.
  */
object Color
{
    val Black = Color(0, 0, 0, 1)

    val White = Color(255, 255, 255, 1)

    val Red = Color(255, 0, 0, 1)

    val Green = Color(0, 255, 0, 1)

    val Blue = Color(0, 0, 255, 1)

    val Transparent = Color(0, 0, 0, 0)

    def fromHex(hexString: String) : Color = {

        val hexLower = hexString.toLowerCase

        //leading #
        val red = hex2dec(hexLower.substring(1,3))
        val green = hex2dec(hexLower.substring(3,5))
        val blue = hex2dec(hexLower.substring(5,7))
        val alpha = hex2dec(hexLower.substring(7,9))/255.0

        new Color(red, green, blue, alpha)
    }

    private def hex2dec(hex: String) : Int = {
        hex.size match {
            case 1 => convertHexToDec(hex)
            case 2 => convertHexToDec(hex.substring(0,1))*16+convertHexToDec(hex.substring(1,2))
        }
    }

    private def convertHexToDec(c: String) : Int = {
        c match {
            case "0" => 0
            case "1" => 1
            case "2" => 2
            case "3" => 3
            case "4" => 4
            case "5" => 5
            case "6" => 6
            case "7" => 7
            case "8" => 8
            case "9" => 9
            case "a" => 10
            case "b" => 11
            case "c" => 12
            case "d" => 13
            case "e" => 14
            case "f" => 15
            case _ => 0
        }
    }
}
