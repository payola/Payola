package controllers.helpers

import cz.payola.scala2json._
import cz.payola.common.rdf._
import cz.payola.common.entities._
import cz.payola.common.entities.plugins.parameters._
import cz.payola.common.entities.plugins._
import cz.payola.common.entities.analyses.PluginInstanceBinding
import cz.payola.common.entities.settings._
import cz.payola.scala2json.rules.BasicSerializationRule
import cz.payola.scala2json.classes.SimpleSerializationClass
import cz.payola.scala2json.rules.CustomValueSerializationRule
import cz.payola.common.geo.Coordinates

// Updated to handle DataCube and connected features [by Jiri Helmich]
class RPCSerializer extends JSONSerializer
{
    val graphClass = new SimpleSerializationClass(classOf[Graph])
    val graphRule = new BasicSerializationRule(Some(classOf[Graph]))
    this.addSerializationRule(graphClass, graphRule)

    val edgeClass = new SimpleSerializationClass(classOf[Edge])
    val edgeRule = new BasicSerializationRule(Some(classOf[Edge]))
    this.addSerializationRule(edgeClass, edgeRule)

    val identifiedNodeClass = new SimpleSerializationClass(classOf[IdentifiedVertex])
    val identifiedNodeRule = new BasicSerializationRule(Some(classOf[IdentifiedVertex]))
    this.addSerializationRule(identifiedNodeClass, identifiedNodeRule)

    val analysisClass = new SimpleSerializationClass(classOf[Analysis])
    val analysisRule = new BasicSerializationRule(
        Some(classOf[Analysis]),
        Some(List(
            "_pluginInstances",
            "_pluginInstanceBindings")
        )
    )
    this.addSerializationRule(analysisClass,analysisRule)

    val analysisPluginInstances = new CustomValueSerializationRule[Analysis]("_pluginInstances", (serializer, analysis) => analysis.pluginInstances)
    this.addSerializationRule(analysisClass, analysisPluginInstances)

    val analysisPluginInstanceBindings = new CustomValueSerializationRule[Analysis]("_pluginInstanceBindings", (serializer, analysis) => analysis.pluginInstanceBindings)
    this.addSerializationRule(analysisClass, analysisPluginInstanceBindings)

    val dataSourceClass = new SimpleSerializationClass(classOf[DataSource])
    val dataSourceRule = new BasicSerializationRule(Some(classOf[DataSource]))
    this.addSerializationRule(dataSourceClass, dataSourceRule)

    val dataSourceParameterValuesRule = new CustomValueSerializationRule[DataSource]("_parameterValues", (serializer, ds) => ds.parameterValues)
    this.addSerializationRule(dataSourceClass, dataSourceParameterValuesRule)

    val pluginInstanceClass = new SimpleSerializationClass(classOf[PluginInstance])
    val pluginInstanceRule = new BasicSerializationRule(
        Some(classOf[PluginInstance]),
        Some(List("_plugin"))
    )
    this.addSerializationRule(pluginInstanceClass, pluginInstanceRule)

    val pluginInstancePlugin = new CustomValueSerializationRule[PluginInstance]("_plugin", (serializer, instance) => instance.plugin)
    this.addSerializationRule(pluginInstanceClass, pluginInstancePlugin)

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
    val defaultValueStringParamRule = new CustomValueSerializationRule[StringParameter]("_defaultValue", (s,v) => v.defaultValue)
    this.addSerializationRule(stringParamClass, stringParamRule)
    this.addSerializationRule(stringParamClass, defaultValueStringParamRule)

    val boolParamClass = new SimpleSerializationClass(classOf[BooleanParameter])
    val boolParamRule = new BasicSerializationRule(Some(classOf[BooleanParameter]))
    this.addSerializationRule(boolParamClass, boolParamRule)

    val floatParamClass = new SimpleSerializationClass(classOf[FloatParameter])
    val floatParamRule = new BasicSerializationRule(Some(classOf[FloatParameter]))
    this.addSerializationRule(floatParamClass, floatParamRule)

    val intParamClass = new SimpleSerializationClass(classOf[IntParameter])
    val intParamRule = new BasicSerializationRule(Some(classOf[IntParameter]))
    this.addSerializationRule(intParamClass, intParamRule)

    val userClass = new SimpleSerializationClass(classOf[User])
    val userRule = new BasicSerializationRule(
        Some(classOf[User]),
        Some(List("_password"))
    )
    this.addSerializationRule(userClass, userRule)

    val groupClass = new SimpleSerializationClass(classOf[Group])
    val groupRule = new BasicSerializationRule(Some(classOf[Group]))
    this.addSerializationRule(groupClass, groupRule)

    val ontologyCustomizationClass = new SimpleSerializationClass(classOf[OntologyCustomization])
    val ontologyCustomizationRule = new BasicSerializationRule(Some(classOf[OntologyCustomization]), Some(List("_classCustomizations")))
    val ontologyCustomizationAdditionalFieldRule = new CustomValueSerializationRule[OntologyCustomization]("_classCustomizations", { case (serializer, customization) => customization.classCustomizations })
    this.addSerializationRule(ontologyCustomizationClass, ontologyCustomizationRule)
    this.addSerializationRule(ontologyCustomizationClass, ontologyCustomizationAdditionalFieldRule)

    val userCustomizationClass = new SimpleSerializationClass(classOf[UserCustomization])
    val userCustomizationClassRule = new BasicSerializationRule(Some(classOf[UserCustomization]), Some(List("_classCustomizations")))
    val userCustomizationAdditionalClassFieldRule = new CustomValueSerializationRule[UserCustomization]("_classCustomizations", { case (serializer, customization) => customization.classCustomizations })
    this.addSerializationRule(userCustomizationClass, userCustomizationClassRule)
    this.addSerializationRule(userCustomizationClass, userCustomizationAdditionalClassFieldRule)

    val classCustomizationClass = new SimpleSerializationClass(classOf[ClassCustomization])
    val classCustomizationRule = new BasicSerializationRule(Some(classOf[ClassCustomization]))
    this.addSerializationRule(classCustomizationClass, classCustomizationRule)

    val propertyCustomizationClass = new SimpleSerializationClass(classOf[PropertyCustomization])
    val propertyCustomizationRule = new BasicSerializationRule(Some(classOf[PropertyCustomization]))
    this.addSerializationRule(propertyCustomizationClass, propertyCustomizationRule)

    val geoCustomizationClass = new SimpleSerializationClass(classOf[Coordinates])
    val geoCustomizationRule = new BasicSerializationRule(Some(classOf[Coordinates]))
    this.addSerializationRule(geoCustomizationClass, geoCustomizationRule)

    val prefixClass = new SimpleSerializationClass(classOf[Prefix])
    val prefixRule = new BasicSerializationRule(Some(classOf[Prefix]))
    this.addSerializationRule(prefixClass, prefixRule)

}
