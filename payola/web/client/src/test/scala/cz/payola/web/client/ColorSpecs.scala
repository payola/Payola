/*package cz.payola.web.client

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import views.plugins.visual.Color
import org.scalatest.Assertions._

class ColorSpecs extends FlatSpec with ShouldMatchers
{
    "Black" should "be black" in {
        val black = Color.Black

        assert(black.red == 0)
        assert(black.green == 0)
        assert(black.blue == 0)
        assert(black.alpha == 1)
    }

    "#ffffffff" should "be white with no transparency" in {
        val white = Color.fromHex("#ffffffff")

        assert(white.red == 255)
        assert(white.green == 255)
        assert(white.blue == 255)
        assert(white.alpha == 1)
    }

    "0x00FF0088" should "be rgba(0,255,0,0.5)" in {
        val white = Color.fromHex("#00FF0088")

        assert(white.red == 0)
        assert(white.green == 255)
        assert(white.blue == 0)
        assert(white.alpha == 0.5)
    }
}   */