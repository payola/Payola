package cz.payola.data.entities

import schema.PayolaDB

class Analysis(
        id: String,
        name: String,
        owner: User)
    extends cz.payola.domain.entities.Analysis(id, name, owner)
    with PersistableEntity
{
    val ownerId: String = if (owner == null) "" else owner.id

    private lazy val _pluginInstancesQuery =  PayolaDB.analysesPluginInstances.left(this)

    override def pluginInstances : collection.Seq[PluginInstanceType] = {
        evaluateCollection(_pluginInstancesQuery)
    }

    override def appendPluginInstance(instance: cz.payola.domain.entities.PluginInstance) {
        super.appendPluginInstance(instance)

        if(instance.isInstanceOf[PluginInstance]) {
            associate(instance.asInstanceOf[PluginInstance], _pluginInstancesQuery)
        }
    }
}
