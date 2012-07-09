package cz.payola.domain.entities.settings.ontology

import cz.payola.domain.Entity
import scala.collection.mutable.ListBuffer

/** Class that represents visual customization of a particular ontology.
  *
  */
class Customization(var name: String, val ontologyURL: String) extends Entity with cz.payola.common.entities.settings.ontology.Customization
{
    /** Class customizations. */
    val classCustomizations: collection.Seq[ClassCustomization] = new ListBuffer[ClassCustomization]()

    /** Property customizations. Note that each class may have its own property customizations
      *  that may override these generic ones.
      */
    val propertyCustomizations: collection.Seq[PropertyCustomization] = new ListBuffer[PropertyCustomization]()
}
