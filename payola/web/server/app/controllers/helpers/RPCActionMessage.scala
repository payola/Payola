package controllers.helpers

case class RPCActionMessage(methodToRun: java.lang.reflect.Method, runnableObject: Any, paramArray: Array[java.lang.Object])
