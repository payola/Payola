package s2js


abstract class JsType

case class JsObject(items: List[(String, JsType)]) extends JsType

case class JsArray(items: List[JsType]) extends JsType

case class JsString(value: String) extends JsType

case class JsNumber(value: Number) extends JsType

case class JsFunction(value: String) extends JsType

case class JsBoolean(value: Boolean) extends JsType