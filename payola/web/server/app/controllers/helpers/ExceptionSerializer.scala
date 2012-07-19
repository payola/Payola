package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules._
import s2js.runtime.shared.rpc
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.CustomValueSerializationRule

class ExceptionSerializer extends JSONSerializer
{
    val rpcExceptionClass = new SimpleSerializationClass(classOf[rpc.Exception])
    val stackTraceDisable = new BasicSerializationRule(Some(classOf[rpc.Exception]), Some(List(
        "stackTrace",
        "cause"
    )))
    this.addSerializationRule(rpcExceptionClass, stackTraceDisable)
}
