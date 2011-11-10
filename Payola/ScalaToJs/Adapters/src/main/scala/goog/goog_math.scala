package goog.math

class Box

class Size(width:Int, height:Int) {
  def getLongest():Int = 0
  def getShortest():Int = 0
  def area():Int = 0
  def perimeter():Int = 0
  def aspectRatio():Int = 0
  def isEmpty:Boolean = false
  def ceil():Size = null
  def fitsInside(target:Size):Boolean = false
  def floor():Size = null
  def round():Size = null
  def scale(s:Int):Size = null
  def scaleToFit(target:Size):Size = null
}

object Size {
  def equals(a:Size, b:Size):Boolean = false
}



