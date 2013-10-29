package cz.payola.common.entities.settings

import cz.payola.common.Entity
import cz.payola.common.entities._
import scala.collection.immutable

trait Customization extends Entity with NamedEntity with OptionallyOwnedEntity with ShareableEntity
{
    /** Type of the class customizations in the ontology customization. */
    type ClassCustomizationType <: ClassCustomization

    /** URL that is used for customization. */
    val URLs: String

    val userDefined: Option[Boolean]

    protected var _classCustomizations: immutable.Seq[ClassCustomizationType]

    /** Customizations of classes in the ontology. */
    def classCustomizations = _classCustomizations

    override def classNameText = "customization"

    def convertToOntologyCustomization(): OntologyCustomization

    def toOntologyCustomization(): Option[OntologyCustomization]

    def convertToUserCustomization(): UserCustomization

    def toUserCustomization(): Option[UserCustomization]

    def isUserDefined = userDefined.isDefined && userDefined.get
}
