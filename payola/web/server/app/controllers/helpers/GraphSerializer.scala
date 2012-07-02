package controllers.helpers

import cz.payola.scala2json.JSONSerializer
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.common.rdf._

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

}
