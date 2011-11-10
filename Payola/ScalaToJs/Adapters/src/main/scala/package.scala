package s2js

case class Html(elem:xml.Elem) extends browser.Element

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

class JsArray {
  def every(fn:(Any, Long, JsArray)=>Boolean) = false
  def forEach(fn:(Any, Long, JsArray)=>Unit) {}
  def map(fn:(Any, Long, JsArray)=>Any) = new JsArray
  def filter(fn:(Any, Long, JsArray)=>Boolean) = new JsArray
  def some(fn:(Any, Long, JsArray)=>Boolean) = false
  def push(x:Any):Unit = {}
  val length:Long = 0
  def apply(elem:Int):Any = null
}

object JsArray {
  def apply(elems:Any*) = new JsArray
}
