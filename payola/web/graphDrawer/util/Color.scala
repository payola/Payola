package cz.payola.graphDrawer.util

/**
 * Created by IntelliJ IDEA.
 * User: Ondrej Kudlacek
 * Date: 12/14/11
 * Time: 1:05 AM
 */

class Color(redComp: Int, greenComp: Int,  blueComp: Int, alphaComp: Double) {

  def red() = redComp;
  def green() = greenComp;
  def blue() = blueComp;
  def alpha() = alphaComp;

  override def toString: String = {
    val result: String = "rgb(" +
      this.red() + ", " +
      this.green() + ", " +
      this.blue() + ", " +
      this.alpha() + ")";

    result;
  }

}