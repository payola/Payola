package cz.payola.common.entities.settings.ontology

import cz.payola.common.entities.Entity

/** This trait is a blue print for the domain class that wraps settings for custom
  * visualization of an ontology.
  *
  */
trait Customization extends Entity
{
    /** Name of the customization. */
    var name: String

    /** URL of the ontology that is being customized. */
    val ontologyURL: String

    /** Class customizations. */
    val classCustomizations: collection.Seq[ClassCustomization]

    /** Property customizations. Note that each class may have its own property customizations
      *  that may override these generic ones.
      */
    val propertyCustomizations: collection.Seq[PropertyCustomization]
}
