package s2js.runtime.shared

class DependencyException(val message: String = "", val cause: Throwable = null)
    extends scala.Exception(message, cause)
