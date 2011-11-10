package scalosure

object script {
  def literal(script:String):Unit = {}
}

object uuid {
  def generate(num:Int):String = ""
}

class JsObject[A] {
  var x:A = _
  def foreach(fn:((String,A))=>Unit):Unit = {}
  def apply(name:String):A = x
  def update(key:String, value:A):Unit = {}
}

object JsObject {
  def apply[A](elems:(String,A)*):JsObject[A] = new JsObject[A]
  def empty[A]():JsObject[A] = new JsObject[A]
}

class JsArray[A] {
  def every(fn:(A, Long, JsArray[A])=>Boolean) = false
  def forEach(fn:(A, Long, JsArray[A])=>Unit) {}
  def map(fn:(A, Long, JsArray[A])=>Any) = new JsArray[A]
  def filter(fn:(A, Long, JsArray[A])=>Boolean) = new JsArray[A]
  def some(fn:(A, Long, JsArray[A])=>Boolean) = false
  def push(x:A):Unit = {}
  val length:Long = 0
  def apply(elem:Int):A = "".asInstanceOf[A]
  def join(separator:String) = ""
}

object JsArray {
  def apply[A](elems:A*):JsArray[A] = new JsArray[A]
  def empty[A]():JsArray[A] = new JsArray[A]
}

class ScalosureObject
