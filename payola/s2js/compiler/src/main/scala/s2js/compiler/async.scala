package s2js.compiler

/**
  * An annotation that marks asynchronous methods of remote objects. It means that the method call returns almost
  * immediately and when the result is known, it's returned using the success callback. If any error occurs during
  * execution, then it's returned using the error callback.
  *
  * The marked method must have three parameter lists, where the first parameter list consists of the actual parameters,
  * the second parameter list is the success callback function and the third parameter list is the error callback
  * function.
  */
class async extends scala.annotation.StaticAnnotation
