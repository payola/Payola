package cz.payola.data.entities

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._
import scala.collection.immutable

class Analysis(name: String, owner: Option[User])
    extends cz.payola.domain.entities.Analysis(name, owner)
    with PersistableEntity
{
    type DomainParameterValueType = cz.payola.domain.entities.analyses.ParameterValue[_]

    private lazy val _pluginInstancesQuery = PayolaDB.analysesPluginInstances.left(this)
    private lazy val _pluginInstancesBindingsQuery = PayolaDB.analysesPluginInstancesBindings.left(this)

    val ownerId: Option[String] = owner.map(_.id)

    override def pluginInstances : collection.Seq[PluginInstanceType] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    override def pluginInstanceBindings: Seq[PluginInstanceBindingType] = {
        evaluateCollection(_pluginInstancesBindingsQuery)
    }

    override def addPluginInstance(instance: PluginInstanceType) {
        super.addPluginInstance(
            instance match {
                // Just associate binding with analysis and persist
                case i: PluginInstance => {
                    associate(i, _pluginInstancesQuery);

                    i
                }
                // "Convert" to data.Binding, associate with analysis and persist
                case i: cz.payola.domain.entities.analyses.PluginInstance => {
                    val inst = new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues))
                    associate(inst, _pluginInstancesQuery)

                    // Now assign paraneter values passed as parameter to PluginINstance
                    inst.associateParameterValues()
                    
                    inst
                }
            }
        )
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

    override def removePluginInstance(instance: PluginInstanceType): Option[PluginInstanceType] = {
        super.removePluginInstance(instance)
        
        if (instance.isInstanceOf[PluginInstance]) {
            instance.asInstanceOf[PluginInstance].analysisId = None
            Some(instance)
        }
        else {
            None
        }
    }

    override def addBinding(binding: PluginInstanceBindingType) {
        super.addBinding(
            binding match {
                // Just associate binding with analysis and persist
                case b: PluginInstanceBinding => {
                    associate(b, _pluginInstancesBindingsQuery)

                    b
                }
                // "Convert" to data.Binding, associate with analysis and persist
                case b: cz.payola.domain.entities.analyses.PluginInstanceBinding => {
                    // "Convert" source and target plugin parameterValues of binding in order to persist them
                    val source = b.sourcePluginInstance match {
                        case i: PluginInstance => i
                        case i: cz.payola.domain.entities.analyses.PluginInstance
                            => new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues))
                    }
                    val target = b.targetPluginInstance match {
                        case i: PluginInstance => i
                        case i: cz.payola.domain.entities.analyses.PluginInstance =>
                            new PluginInstance(i.id, i.plugin, convertParamValues(i.parameterValues))
                    }

                    // "Convert" binding, associate with analysis and persist
                    val bin = new PluginInstanceBinding(source, target, b.targetInputIndex)
                    associate(bin, _pluginInstancesBindingsQuery)

                    bin
                }
            }
        )
    }
    override def removeBinding(binding: PluginInstanceBindingType): Option[PluginInstanceBindingType] = {
        super.removeBinding(binding)

        if (binding.isInstanceOf[PluginInstanceBinding]) {
            binding.asInstanceOf[PluginInstanceBinding].analysisId = None
            Some(binding)
        }
        else {
            None
        }

    }
}
