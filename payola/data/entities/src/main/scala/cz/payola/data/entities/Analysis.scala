package cz.payola.data.entities

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._
import scala.collection.immutable

object Analysis {

    def apply(a: cz.payola.common.entities.Analysis): Analysis = {
        val owner = if (a.owner.isDefined) Some(User(a.owner.get)) else None
        new Analysis(a.id, a.name, owner)
    }
}

class Analysis(
    override val id: String,
    name: String,
    owner: Option[User])
    extends cz.payola.domain.entities.Analysis(name, owner)
    with PersistableEntity
{
    type DomainParameterValueType = cz.payola.domain.entities.analyses.ParameterValue[_]

    private lazy val _pluginInstancesQuery = PayolaDB.analysesPluginInstances.left(this)
    private lazy val _pluginInstancesBindingsQuery = PayolaDB.analysesPluginInstancesBindings.left(this)

    val ownerId: Option[String] = owner.map(_.id)

    override def pluginInstances : collection.Seq[PluginInstanceType] = {        
        println("Instances?")

        val r = evaluateCollection(_pluginInstancesQuery)
        
        println("Instances " + r.size)
        
        r
        
    }

    override def pluginInstanceBindings: Seq[PluginInstanceBindingType] = {
        println("Bindings?")
        
        val r = evaluateCollection(_pluginInstancesBindingsQuery)
        
        println("Bindings " + r.size)
        
        r
    }

    override protected def storePluginInstance(instance: Analysis#PluginInstanceType) {
        instance match {
            // Just associate binding with analysis and persist
            case i: PluginInstance => associate(i, _pluginInstancesQuery)

            // "Convert" to data.PluginInstance, associate with analysis and persist
            case i: cz.payola.domain.entities.analyses.PluginInstance => {
                val inst = new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues), i.description)
                associate(inst, _pluginInstancesQuery)
            }
        }
    }

    override protected def discardPluginInstance(instance: Analysis#PluginInstanceType) {
        if (instance.isInstanceOf[PluginInstance]) {
            instance.asInstanceOf[PluginInstance].analysisId = None
        }
    }

    override protected def storeBinding(binding: Analysis#PluginInstanceBindingType) {
        binding match {
            // Just associate binding with analysis and persist
            case b: PluginInstanceBinding => associate(b, _pluginInstancesBindingsQuery)

            // "Convert" to data.Binding, associate with analysis and persist
            case b: cz.payola.domain.entities.analyses.PluginInstanceBinding => {
                // "Convert" source and target plugin parameterValues of binding in order to persist them
                val source = b.sourcePluginInstance match {
                    case i: PluginInstance => i
                    case i: cz.payola.domain.entities.analyses.PluginInstance
                        => new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues), i.description)
                }
                val target = b.targetPluginInstance match {
                    case i: PluginInstance => i
                    case i: cz.payola.domain.entities.analyses.PluginInstance =>
                        new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues), i.description)
                }

                // "Convert" binding, associate with analysis and persist
                val bin = new PluginInstanceBinding(b.id, source, target, b.targetInputIndex)
                associate(bin, _pluginInstancesBindingsQuery)
            }
        }
    }

    override protected def discardBinding(binding: Analysis#PluginInstanceBindingType) {
        if (binding.isInstanceOf[PluginInstanceBinding]) {
                binding.asInstanceOf[PluginInstanceBinding].analysisId = None
        }
    }

    private def convertParamValues(values: immutable.Seq[DomainParameterValueType]): immutable.Seq[ParameterValue[_]] = {

        // Every parameter value needs to be data.ParameterValue
        values.map {
            case b: BooleanParameterValue => b
            case f: FloatParameterValue => f
            case i: IntParameterValue => i
            case s: StringParameterValue => s
            case b: cz.payola.domain.entities.analyses.parameters.BooleanParameterValue
                    => new BooleanParameterValue(
                            b.id,
                            new BooleanParameter(b.parameter.id, b.parameter.name, b.parameter.defaultValue),
                            b.value
                        )
            case f: cz.payola.domain.entities.analyses.parameters.FloatParameterValue
                    => new FloatParameterValue(
                            f.id,
                            new FloatParameter(f.parameter.id, f.parameter.name, f.parameter.defaultValue),
                            f.value
                        )
            case i: cz.payola.domain.entities.analyses.parameters.IntParameterValue
                    => new IntParameterValue(
                            i.id,
                            new IntParameter(i.parameter.id, i.parameter.name, i.parameter.defaultValue),
                            i.value
                        )
            case s: cz.payola.domain.entities.analyses.parameters.StringParameterValue
                    => new StringParameterValue(
                            s.id,
                            new StringParameter(s.parameter.id, s.parameter.name, s.parameter.defaultValue),
                            s.value
                        )
        }
    }
}
