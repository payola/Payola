package controllers

case class InvocationInfo(methodToRun: java.lang.reflect.Method, clazz: java.lang.Class[_], runnableObj: Any)
