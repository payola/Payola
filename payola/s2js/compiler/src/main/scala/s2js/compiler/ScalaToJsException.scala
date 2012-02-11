package s2js.compiler

/** A Scala to JavaScript compiler exception */
case class ScalaToJsException(errorMsg: String) extends RuntimeException(errorMsg)
