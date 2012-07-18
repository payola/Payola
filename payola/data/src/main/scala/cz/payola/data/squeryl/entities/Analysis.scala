package cz.payola.data.squeryl.entities

import cz.payola.data.squeryl.entities.analyses._
import cz.payola.data.squeryl.entities.plugins.PluginInstance
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.Analysis]] to [[cz.payola.data.squeryl.entities.Analysis]]
  */
object Analysis extends EntityConverter[Analysis]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Analysis] = {
        entity match {
            case e: Analysis => Some(e)
            case e: cz.payola.common.entities.Analysis
                    => Some(new Analysis(e.id, e.name, e.owner.map(User(_)), e.isPublic, e.description))
            case _ => None
        }
    }
}

class Analysis(override val id: String, name: String, o: Option[User], var _isPub: Boolean, var _desc: String)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.Analysis(name, o)
    with PersistableEntity with OptionallyOwnedEntity with ShareableEntity with NamedEntity with DescribedEntity
{
    type DomainParameterValueType = plugins.ParameterValue[_]

    _pluginInstances = null;
    private lazy val _pluginInstancesQuery = context.schema.analysesPluginInstances.left(this)

    _pluginInstanceBindings = null
    private lazy val _pluginInstancesBindingsQuery = context.schema.analysesPluginInstancesBindings.left(this)
    
    _defaultCustomization = null
    
    override def defaultOntologyCustomization_=(value: Option[Analysis#OntologyCustomizationType]) {
        wrapInTransaction{
            _defaultCustomization = context.analysisRepository.setDefaultOntologyCustomization(id, value)
        }
    }

    override def defaultOntologyCustomization = {
        if (_defaultCustomization == null) {
            context.analysisRepository.loadDefaultOntology(this)
        }

        _defaultCustomization
    }

    override def pluginInstances: immutable.Seq[PluginInstanceType] = {
        if (_pluginInstances == null) {
            context.analysisRepository.loadPluginInstances(this)
        }

        _pluginInstances.toList
    }

    def pluginInstances_=(value: Seq[PluginInstanceType]) {
        _pluginInstances =  mutable.ArrayBuffer(value: _*)
    }

    override def pluginInstanceBindings: immutable.Seq[PluginInstanceBindingType] = {
        if (_pluginInstanceBindings == null) {
            context.analysisRepository.loadPluginInstanceBindings(this)
        }

        _pluginInstanceBindings.toList
    }

    def pluginInstanceBindings_=(value: Seq[PluginInstanceBindingType]){
        _pluginInstanceBindings = mutable.ArrayBuffer(value: _*)
    }

    override protected def storePluginInstance(instance: Analysis#PluginInstanceType) {
        super.storePluginInstance(associatePluginInstance(PluginInstance(instance)))
    }

    override protected def discardPluginInstance(instance: Analysis#PluginInstanceType) {
        context.analysisRepository.removePluginInstanceById(instance.id)

        super.discardPluginInstance(instance)
    }

    override protected def storeBinding(binding: Analysis#PluginInstanceBindingType) {
        super.storeBinding(associatePluginInstanceBinding(PluginInstanceBinding(binding)))
    }

    override protected def discardBinding(binding: Analysis#PluginInstanceBindingType) {
        context.analysisRepository.removePluginInstanceBindingById(binding.id)

        super.discardBinding(binding)
    }

    def associatePluginInstance(instance: PluginInstance): PluginInstance = {
        context.schema.associate(instance, _pluginInstancesQuery)

        context.analysisRepository.persistPluginInstance(instance)

        instance
    }

    def associatePluginInstanceBinding(instance: PluginInstanceBinding): PluginInstanceBinding = {
        context.schema.associate(instance, _pluginInstancesBindingsQuery)
    }
}
