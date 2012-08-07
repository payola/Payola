package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.BasicSerializationRule

class ExceptionSerializer extends JSONSerializer
{
    val exceptionClass = new SimpleSerializationClass(classOf[Exception])
    val disableJavaExceptionFields = new BasicSerializationRule(None, Some(List(
        "stackTrace",
        "serialVersionUID",
        "UNASSIGNED_STACK",
        "SUPPRESSED_SENTINEL",
        "suppressedExceptions",
        "NULL_CAUSE_MESSAGE",
        "SELF_SUPPRESSION_MESSAGE",
        "CAUSE_CAPTION",
        "SUPPRESSED_CAPTION",
        "EMPTY_THROWABLE_ARRAY",
        "$assertionsDisabled"
    )))

    addSerializationRule(exceptionClass, disableJavaExceptionFields)
}
