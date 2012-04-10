package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.CustomValueSerializationRule

class ExceptionSerializer extends JSONSerializer
{
    val rule = new CustomValueSerializationRule[rpc.Exception]("message", { (_, exc) => exc.message })
    this.addSerializationRule(new SimpleSerializationClass(classOf[rpc.Exception]), rule)
}
