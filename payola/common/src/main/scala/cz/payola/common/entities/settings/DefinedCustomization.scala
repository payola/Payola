package cz.payola.common.entities.settings

import cz.payola.common.Entity
import cz.payola.common.entities._
import scala.collection.immutable

trait DefinedCustomization extends Entity with NamedEntity
{
    /** URL that is used for customization. */
    val URLs: String

    /** Type of the class customizations in the ontology customization. */
    type ClassCustomizationType <: ClassCustomization

    protected var _classCustomizations: immutable.Seq[ClassCustomizationType]

    /** Customizations of classes in the ontology. */
    def classCustomizations = _classCustomizations.filter(!_.isConditionalCustomization)
}
