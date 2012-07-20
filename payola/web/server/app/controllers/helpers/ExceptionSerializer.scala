package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.rules._
import cz.payola.scala2json.classes.SimpleSerializationClass

class ExceptionSerializer extends JSONSerializer
{
    val exceptionClass = new SimpleSerializationClass(classOf[Exception])
    val disableStackTrace = new BasicSerializationRule(None, Some(List("stackTrace")))
    addSerializationRule(exceptionClass, disableStackTrace)
}
