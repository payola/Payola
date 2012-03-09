package s2js.compiler

/**
  * An annotation that makes the compiler replace the class, method or val compiled body with the specified JavaScript
  * code. If it annotates a method, jsCode should be just the method body. If it annotates a val, then the jsCode
  * would be put on the right side of the assignment to the field.
  */
class javascript(val jsCode: String) extends scala.annotation.StaticAnnotation
