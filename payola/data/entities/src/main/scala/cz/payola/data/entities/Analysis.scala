package cz.payola.data.entities

import cz.payola.data.entities.analyses.{PluginInstance, PluginInstanceBinding}

class Analysis(name: String, owner: Option[User])
    extends cz.payola.domain.entities.Analysis(name, owner)
    with PersistableEntity
{
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
        super.addPluginInstance(instance)
        
        if (instance.isInstanceOf[PluginInstance]) {
            associate(instance.asInstanceOf[PluginInstance], _pluginInstancesQuery)
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

        super.addBinding(binding)

        if (binding.isInstanceOf[PluginInstanceBinding]){
            associate(binding.asInstanceOf[PluginInstanceBinding], _pluginInstancesBindingsQuery)
        }
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
