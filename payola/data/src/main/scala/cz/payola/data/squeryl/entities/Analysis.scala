package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.analyses._
import org.squeryl.annotations.Transient
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import scala.collection.immutable
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.Analysis]] to [[cz.payola.data.squeryl.entities.Analysis]]
  */
object Analysis extends EntityConverter[Analysis]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Analysis] = {
        entity match {
            case e: Analysis => Some(e)
            case e: cz.payola.common.entities.Analysis => Some(new Analysis(e.id, e.name, e.owner.map(User(_))))
            case _ => None
        }
    }
}

class Analysis(override val id: String, name: String, o: Option[User])(implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Analysis(name, o)
    with PersistableEntity with OptionallyOwnedEntity
{
    type DomainParameterValueType = plugins.ParameterValue[_]

    @Transient
    private var _pluginInstancesLoaded = false;
    private lazy val _pluginInstancesQuery = context.schema.analysesPluginInstances.left(this)

    @Transient
    private var _pluginInstancesBindingsLoaded = false
    private lazy val _pluginInstancesBindingsQuery = context.schema.analysesPluginInstancesBindings.left(this)

    override def pluginInstances: immutable.Seq[PluginInstanceType] = {
        // Lazy-load related instances only for first time
        if (!_pluginInstancesLoaded) {
            wrapInTransaction {
                _pluginInstancesQuery.toList.foreach(i =>
                    if (!super.pluginInstances.contains(i)) {
                        super.storePluginInstance(i)
                    }
                )
            }

            _pluginInstancesLoaded = true
        }

        super.pluginInstances
    }

    override def pluginInstanceBindings: immutable.Seq[PluginInstanceBindingType] = {
        // Lazy-load related bindings only for first time
        if (!_pluginInstancesBindingsLoaded) {
            wrapInTransaction {
                _pluginInstancesBindingsQuery.toList.foreach(b =>
                    if (!super.pluginInstanceBindings.contains(b)) {
                        super.storeBinding(b)
                    }
                )
            }

            _pluginInstancesBindingsLoaded = true
        }
        
        super.pluginInstanceBindings
    }

    override protected def storePluginInstance(instance: Analysis#PluginInstanceType) {
        super.storePluginInstance(associatePluginInstance(PluginInstance(instance)))
    }

    override protected def discardPluginInstance(instance: Analysis#PluginInstanceType) {
        context.pluginInstanceRepository.removeById(instance.id)

        super.discardPluginInstance(instance)
    }

    override protected def storeBinding(binding: Analysis#PluginInstanceBindingType) {
        super.storeBinding(associatePluginInstanceBinding(PluginInstanceBinding(binding)))
    }

    override protected def discardBinding(binding: Analysis#PluginInstanceBindingType) {
        context.pluginInstanceBindingRepository.removeById(binding.id)

        super.discardBinding(binding)
    }

    def associatePluginInstance(instance: PluginInstance): PluginInstance = {
        associate(instance, _pluginInstancesQuery)

        context.pluginInstanceRepository.persist(instance)

        instance
    }

    def associatePluginInstanceBinding(instance: PluginInstanceBinding): PluginInstanceBinding = {
        associate(instance, _pluginInstancesBindingsQuery)
    }
}
