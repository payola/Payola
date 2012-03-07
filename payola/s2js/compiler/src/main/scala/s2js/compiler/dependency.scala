package s2js.compiler

/**
  * An annotation that informs the compiler about a dependency in the native JavaScript code.
  */
class dependency(val symbolFullName: String) extends scala.annotation.StaticAnnotation
