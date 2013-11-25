package cz.payola.common.entities.settings

import cz.payola.common.entities._
import cz.payola.common.Entity
import scala.collection.immutable

/**
  * Customization of a graph appearance based on an ontology.
  */
trait OntologyCustomization extends DefinedCustomization with OptionallyOwnedEntity with ShareableEntity
{
    override def classNameText = "ontology customization"

    /** URL that is used for customization. */
    val URLs: String

    /** Type of the class customizations in the ontology customization. */
    type ClassCustomizationType <: ClassCustomization

    protected var _classCustomizations: immutable.Seq[ClassCustomizationType]

    /** Customizations of classes in the ontology. */
    override def classCustomizations = _classCustomizations.filter(!_.isConditionalCustomization)
}
