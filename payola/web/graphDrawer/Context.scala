package cz.payola.graphDrawer

import util.Color

/**
 * Created by IntelliJ IDEA.
 * User: Ondrej Kudlacek
 * Date: 12/14/11
 * Time: 10:14 PM
 */

class Context {

  //TODO this has to be replaced with Context field of the HTML tag for JavaScript

  var strokeStyle: String = "";
  var fillStyle: String = "";
  var lineWidth: Int = 1;
  var font: String = "18px Sans";
  var textAlign: String = "center";

  def fillText(text: String, locationX: Int, locationY: Int) {}

  def beginPath() {}
  def moveTo(x: Int,  y: Int) {}
  def lineTo(x: Int, y: Int) {}
  def closePath() {}

  def fillRect(x: Int, y: Int, width: Int, height: Int) {}

  def stroke() {}
  def fill() {}

  def bezierCurveTo(ctrl1X: Int, ctrl1Y: Int, ctrl2X: Int, ctrl2Y: Int, x: Int, y: Int) {}
  def quadraticCurveTo(x1: Int, y1: Int, x2: Int, y2: Int) {}
}