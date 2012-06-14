package cz.payola.common.entities.settings.ontology

/** A trait that defines common properties of ClassCustomization
  * and PropertyCustomization traits.
  *
  */
private[ontology] trait OntologyEntityCustomization
{
    /** URI of the class or property */
    val URI: String

    /** Stroke width. */
    var strokeWidth: Int = 0

    /** CSS stroke style. */
    var strokeStyle: String = ""
}
