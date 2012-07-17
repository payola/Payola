package controllers.helpers

import cz.payola.scala2json._
import cz.payola.common.rdf._
import cz.payola.common.entities._
import cz.payola.common.entities.plugins.parameters._
import cz.payola.scala2json.rules.CustomValueSerializationRule
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass
import scala.Some
import cz.payola.common.entities.plugins._
import cz.payola.common.entities.analyses.PluginInstanceBinding
import cz.payola.scala2json.rules.CustomValueSerializationRule
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass
import scala.Some
import cz.payola.domain.entities.plugins.parameters.StringParameterValue
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass
import scala.Some
import cz.payola.scala2json.rules.CustomValueSerializationRule

class RPCSerializer extends JSONSerializer
{
    val graphClass = new SimpleSerializationClass(classOf[Graph])
    val graphRule = new BasicSerializationRule(Some(classOf[Graph]))
    this.addSerializationRule(graphClass, graphRule)

    val edgeClass = new SimpleSerializationClass(classOf[Edge])
    val edgeRule = new BasicSerializationRule(Some(classOf[Edge]))
    this.addSerializationRule(edgeClass, edgeRule)

    val literalNodeClass = new SimpleSerializationClass(classOf[LiteralVertex])
    val literalNodeRule = new BasicSerializationRule(Some(classOf[LiteralVertex]), Some(List("value")))
    this.addSerializationRule(literalNodeClass, literalNodeRule)

    // Always serialize the literal vertex value as a string because it may be almost anything.
    this.addSerializationRule(literalNodeClass, new CustomValueSerializationRule[LiteralVertex](
        "value", (serializer, literalVertex) => literalVertex.value.toString))

    val identifiedNodeClass = new SimpleSerializationClass(classOf[IdentifiedVertex])
    val identifiedNodeRule = new BasicSerializationRule(Some(classOf[IdentifiedVertex]))
    this.addSerializationRule(identifiedNodeClass, identifiedNodeRule)

    val analysisClass = new SimpleSerializationClass(classOf[Analysis])
    val analysisRule = new BasicSerializationRule(
        Some(classOf[Analysis]),
        Some(List("cz$payola$common$entities$Analysis$$_pluginInstances","cz$payola$common$entities$Analysis$$_pluginInstanceBindings"))
    )
    this.addSerializationRule(analysisClass,analysisRule)

    val analysisPluginInstances = new CustomValueSerializationRule[Analysis]("_pluginInstances", (serializer, analysis) => analysis.pluginInstances)
    this.addSerializationRule(analysisClass,analysisPluginInstances)

    val analysisPluginInstanceBindings = new CustomValueSerializationRule[Analysis]("_pluginInstanceBindings", (serializer, analysis) => analysis.pluginInstanceBindings)
    this.addSerializationRule(analysisClass,analysisPluginInstanceBindings)

    val pluginInstanceClass = new SimpleSerializationClass(classOf[PluginInstance])
    val pluginInstanceRule = new BasicSerializationRule(Some(classOf[PluginInstance]))
    this.addSerializationRule(pluginInstanceClass, pluginInstanceRule)

    val pluginInstancePlugin = new CustomValueSerializationRule[PluginInstance]("_plugin", (serializer, instance) => instance.plugin)
    this.addSerializationRule(pluginInstanceClass,pluginInstancePlugin)

    val pluginInstanceBindingClass = new SimpleSerializationClass(classOf[PluginInstanceBinding])
    val pluginInstanceBindingRule = new BasicSerializationRule(Some(classOf[PluginInstanceBinding]))
    this.addSerializationRule(pluginInstanceBindingClass, pluginInstanceBindingRule)

    val pluginInstanceBindingRuleSource = new CustomValueSerializationRule[PluginInstanceBinding]("_targetPluginInstance", (serializer, b) => b.targetPluginInstance)
    this.addSerializationRule(pluginInstanceBindingClass, pluginInstanceBindingRuleSource)
    val pluginInstanceBindingRuleTarget = new CustomValueSerializationRule[PluginInstanceBinding]("_sourcePluginInstance", (serializer, b) => b.sourcePluginInstance)
    this.addSerializationRule(pluginInstanceBindingClass, pluginInstanceBindingRuleTarget)

    val pluginClass = new SimpleSerializationClass(classOf[Plugin])
    val pluginRule = new BasicSerializationRule(Some(classOf[Plugin]))
    this.addSerializationRule(pluginClass, pluginRule)

    val stringParamValueClass = new SimpleSerializationClass(classOf[ParameterValue[_]])
    val stringParamValueRule = new BasicSerializationRule(Some(classOf[ParameterValue[_]]), Some(List("value")))
    val paramValueAlias = new CustomValueSerializationRule[ParameterValue[_]]("_value", (serializer, v) => v.value)
    this.addSerializationRule(stringParamValueClass, stringParamValueRule)
    this.addSerializationRule(stringParamValueClass, paramValueAlias)

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

    val userClass = new SimpleSerializationClass(classOf[User])
    val userRule = new BasicSerializationRule(Some(classOf[User]))
    this.addSerializationRule(userClass, userRule)

}
