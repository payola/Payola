package cz.payola.common.entities.settings

import cz.payola.common.entities._
import cz.payola.common.Entity
import scala.collection.immutable

/**
  * Customization of a graph appearance based on an ontology.
  */
trait OntologyCustomization extends Entity with NamedEntity with OptionallyOwnedEntity with ShareableEntity
{
    /** Type of the class customizations in the ontology customization. */
    type ClassCustomizationType <: ClassCustomization

    /** URL of the ontology that is used for customization. */
    val ontologyURLs: String

    protected var _classCustomizations: immutable.Seq[ClassCustomizationType]

    override def classNameText = "ontology customization"

    /** Customizations of classes in the ontology. */
    def classCustomizations = _classCustomizations
}
