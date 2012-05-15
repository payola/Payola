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

    /**
      * Adds a new plugin instance binding to the analysis.
      * @param sourcePluginInstance The source plugin instance.
      * @param targetPluginInstance The target plugin instance.
      * @param inputIndex Index of the target plugin instance input the binding is connected to.
      */
    override def addBinding(sourcePluginInstance: PluginInstanceType, targetPluginInstance: PluginInstanceType, inputIndex: Int = 0) {
        // TODO: can't call super.addBinding(..,..,..) becaure creater binding won't persist
        // TODO: would have to create my own biding and assure that both parameters are from data.entities, not domain.entities -> overhead
        require(false, "Method with created binding should be called instead of this")
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
