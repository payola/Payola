package s2js.compiler

/**
  * An annotation that marks methods of remote objects. It makes the compiler ignore the last parameter passed to a
  * remote method (the parameter should correspond to a security context, which may be for example an user instance).
  * The server side is therefore responsible for instantiation of the security context, because it's not sent from
  * the client unlike all other parameters.
  */
class secured extends scala.annotation.StaticAnnotation