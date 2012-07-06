package cz.payola.data.entities

import cz.payola.data.entities.analyses._
import cz.payola.data._
import org.squeryl.annotations.Transient
import cz.payola.data.entities.plugins.PluginInstance
import scala.collection.immutable

/**
  * This object converts [[cz.payola.common.entities.Analysis]] to [[cz.payola.data.entities.Analysis]]
  */
object Analysis {

    def apply(a: cz.payola.common.entities.Analysis)(implicit context: SquerylDataContextComponent): Analysis = {
        a match {
            case analysis: Analysis => analysis
            case _ => new Analysis(a.id, a.name, a.owner.map(User(_)))
        }
    }
}

class Analysis(override val id: String, name: String, o: Option[User])(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Analysis(name, o)
    with PersistableEntity
{
    type DomainParameterValueType = plugins.ParameterValue[_]

    @Transient
    private var _pluginInstancesLoaded = false;
    private lazy val _pluginInstancesQuery = context.schema.analysesPluginInstances.left(this)

    @Transient
    private var _pluginInstancesBindingsLoaded = false
    private lazy val _pluginInstancesBindingsQuery = context.schema.analysesPluginInstancesBindings.left(this)

    var ownerId: Option[String] = o.map(_.id)
    private lazy val _ownerQuery = context.schema.analysisOwnership.right(this)

    override def pluginInstances: immutable.Seq[PluginInstanceType] = {
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

    override def owner: Option[UserType] = {
        if (_owner == None){
            if (ownerId != null && ownerId.isDefined) {
                _owner = evaluateCollection(_ownerQuery).headOption
            }
        }

        _owner
    }

    override def pluginInstanceBindings: immutable.Seq[PluginInstanceBindingType] = {
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
        super.storePluginInstance(associate(i, _pluginInstancesQuery))

        // SQUERYL: Paramters can be associated after the plugin instance is persisted
        i.associateParameterValues()
    }

    override protected def discardPluginInstance(instance: Analysis#PluginInstanceType) {
        context.pluginInstanceDAO.removeById(instance.id)

        super.discardPluginInstance(instance)
    }

    override protected def storeBinding(binding: Analysis#PluginInstanceBindingType) {
        super.storeBinding(associate(PluginInstanceBinding(binding), _pluginInstancesBindingsQuery))
    }

    override protected def discardBinding(binding: Analysis#PluginInstanceBindingType) {
        context.pluginInstanceBindingDAO.removeById(binding.id)

        super.discardBinding(binding)
    }
}
