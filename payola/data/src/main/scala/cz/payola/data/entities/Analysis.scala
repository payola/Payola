package cz.payola.data.entities

import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._
import scala.collection.mutable
import cz.payola.data.PayolaDB
import cz.payola.data.dao.{PluginInstanceDAO, PluginInstanceBindingDAO}
import org.squeryl.annotations.Transient

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

    @Transient
    private var _pluginInstancesLoaded = false;
    private lazy val _pluginInstancesQuery = PayolaDB.analysesPluginInstances.left(this)

    @Transient
    private var _pluginInstancesBindingsLoaded = false;
    private lazy val _pluginInstancesBindingsQuery = PayolaDB.analysesPluginInstancesBindings.left(this)

    val ownerId: Option[String] = owner.map(_.id)

    override def pluginInstances : Seq[PluginInstanceType] = {
        // Lazy-load related instances only for first time
        if (!_pluginInstancesLoaded) {
            evaluateCollection(_pluginInstancesQuery).map( i =>
                if (!super.pluginInstances.contains(i)) {
                    super.storePluginInstance(i)
                }
            )

            _pluginInstancesLoaded = true
        }

        super.pluginInstances
    }
        

    override def pluginInstanceBindings: Seq[PluginInstanceBindingType] = {
        // Lazy-load related bindings only for first time
        if (!_pluginInstancesBindingsLoaded) {
            evaluateCollection(_pluginInstancesBindingsQuery).map(b =>
                if (!super.pluginInstanceBindings.contains(b)) {
                    super.storeBinding(b)
                }
            )

            _pluginInstancesBindingsLoaded = true
        }
        
        super.pluginInstanceBindings
    }

    override protected def storePluginInstance(instance: Analysis#PluginInstanceType) {
        val i = PluginInstance(instance)
        super.storePluginInstance(associate(i, _pluginInstancesQuery).get)

        // Needs to be done after pluin instance is persisted (SQUERYL)
        i.associateParameterValues()
    }

    override protected def discardPluginInstance(instance: Analysis#PluginInstanceType) {
        val i = PluginInstance(instance)
        i.analysisId = None

        // TODO: SQUERYL - ugly
        new PluginInstanceDAO().persist(i)

        super.discardPluginInstance(i)
    }

    override protected def storeBinding(binding: Analysis#PluginInstanceBindingType) {
        super.storeBinding(associate(PluginInstanceBinding(binding), _pluginInstancesBindingsQuery).get)
    }

    override protected def discardBinding(binding: Analysis#PluginInstanceBindingType) {
        val b = PluginInstanceBinding(binding)
        b.analysisId = None

        // TODO: SQUERYL - ugly
        new PluginInstanceBindingDAO().persist(b)

        super.discardBinding(b)
    }
}
