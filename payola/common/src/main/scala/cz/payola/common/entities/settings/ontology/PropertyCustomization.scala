package cz.payola.common.entities.settings.ontology

/** This trait is a blue print for the domain class that wraps settings for custom
  * visualization of an ontology property.
  *
  */
trait PropertyCustomization extends OntologyEntityCustomization
{
    var straightenFactor: Int = 0
}
