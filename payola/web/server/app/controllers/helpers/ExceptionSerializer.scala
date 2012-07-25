package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.rules._
import cz.payola.scala2json.classes.SimpleSerializationClass
import s2js.runtime.shared.rpc._
import cz.payola.scala2json.classes.SimpleSerializationClass
import scala.Some
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.common.exception._
import cz.payola.scala2json.classes.SimpleSerializationClass
import scala.Some
import cz.payola.scala2json.rules.BasicSerializationRule

class ExceptionSerializer extends JSONSerializer
{
    val exceptionClass = new SimpleSerializationClass(classOf[Exception])
    val disableStackTrace = new BasicSerializationRule(None, Some(List("stackTrace")))
    addSerializationRule(exceptionClass, disableStackTrace)

    val rpcExceptionClass = new SimpleSerializationClass(classOf[RpcException])
    val rpcExceptionSerializationRule = new BasicSerializationRule(Some(classOf[RpcExceptionTrait]))
    addSerializationRule(rpcExceptionClass, rpcExceptionSerializationRule)

    val validationExceptionClass = new SimpleSerializationClass(classOf[ValidationException])
    val validationExceptionSerializationRule = new BasicSerializationRule(Some(classOf[ValidationExceptionTrait]))
    addSerializationRule(validationExceptionClass, validationExceptionSerializationRule)
}
