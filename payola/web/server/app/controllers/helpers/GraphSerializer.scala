package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.common.rdf._
import cz.payola.common.entities.Plugin
import cz.payola.common.entities.plugins.parameters._
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.BasicSerializationRule
import scala.Some

class GraphSerializer extends JSONSerializer
{

    val graphClass = new SimpleSerializationClass(classOf[Graph])
    val graphRule = new BasicSerializationRule(Some(classOf[Graph]))
    this.addSerializationRule(graphClass, graphRule)

    val edgeClass = new SimpleSerializationClass(classOf[Edge])
    val edgeRule = new BasicSerializationRule(Some(classOf[Edge]))
    this.addSerializationRule(edgeClass, edgeRule)

    val literalNodeClass = new SimpleSerializationClass(classOf[LiteralVertex])
    val literalNodeRule = new BasicSerializationRule(Some(classOf[LiteralVertex]))
    this.addSerializationRule(literalNodeClass, literalNodeRule)

    val identifiedNodeClass = new SimpleSerializationClass(classOf[IdentifiedVertex])
    val identifiedNodeRule = new BasicSerializationRule(Some(classOf[IdentifiedVertex]))
    this.addSerializationRule(identifiedNodeClass, identifiedNodeRule)



    val pluginClass = new SimpleSerializationClass(classOf[Plugin])
    val pluginRule = new BasicSerializationRule(Some(classOf[Plugin]))
    this.addSerializationRule(pluginClass, pluginRule)

    val stringParamClass = new SimpleSerializationClass(classOf[StringParameter])
    val stringParamRule = new BasicSerializationRule(Some(classOf[StringParameter]))
    this.addSerializationRule(stringParamClass, stringParamRule)

    val boolParamClass = new SimpleSerializationClass(classOf[BooleanParameter])
    val boolParamRule = new BasicSerializationRule(Some(classOf[StringParameter]))
    this.addSerializationRule(boolParamClass, boolParamRule)

    val floatParamClass = new SimpleSerializationClass(classOf[FloatParameter])
    val floatParamRule = new BasicSerializationRule(Some(classOf[FloatParameter]))
    this.addSerializationRule(floatParamClass, floatParamRule)

    val intParamClass = new SimpleSerializationClass(classOf[IntParameter])
    val intParamRule = new BasicSerializationRule(Some(classOf[IntParameter]))
    this.addSerializationRule(intParamClass, intParamRule)

}
