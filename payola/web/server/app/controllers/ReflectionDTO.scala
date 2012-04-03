package controllers

/**
  *
  * @author jirihelmich
  * @created 4/3/12 5:12 PM
  * @package controllers
  */

case class ReflectionDTO(methodToRun: java.lang.reflect.Method, clazz: java.lang.Class[_], runnableObj: Any)
