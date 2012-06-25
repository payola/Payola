package cz.payola.common.entities.settings.ontology

/** This trait is a blue print for the domain class that wraps settings for custom
  * visualization of an ontology class.
  *
  */
trait ClassCustomization extends OntologyEntityCustomization
{
    var fillStyle: String = ""
    var radius: Int = 0
    var glyph: Option[Char] = None

    val propertyCustomizations: collection.Seq[PropertyCustomization]
}
