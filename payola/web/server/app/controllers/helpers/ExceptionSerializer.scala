package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules._
import s2js.runtime.shared.rpc
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.CustomValueSerializationRule

class ExceptionSerializer extends JSONSerializer
{
    def getStackTrace(ex: java.lang.Throwable) : String = {
        ex.getStackTrace.map(_.toString).mkString("\n")
    }

    val rpcExceptionClass = new SimpleSerializationClass(classOf[rpc.Exception])
    val stackTraceDisable = new BasicSerializationRule(Some(classOf[rpc.Exception]), Some(List("stackTrace")))
    val stackTraceRule = new CustomValueSerializationRule[rpc.Exception]("stackTrace", {(ser, e) =>
        var trace = ""
        var ex: java.lang.Throwable = e
        while (ex != null)
        {
            trace += getStackTrace(ex)
            ex = ex.getCause
        }
        trace
    })

    this.addSerializationRule(rpcExceptionClass, stackTraceDisable)
    this.addSerializationRule(rpcExceptionClass, stackTraceRule)
}
