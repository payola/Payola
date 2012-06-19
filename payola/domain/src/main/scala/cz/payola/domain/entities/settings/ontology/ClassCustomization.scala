package cz.payola.domain.entities.settings.ontology

import cz.payola.domain.entities.Entity
import scala.collection.mutable.ListBuffer

/** This class represents ontology class visual customization.
  */
class ClassCustomization(val URI: String) extends Entity with cz.payola.common.entities.settings.ontology.ClassCustomization
{
    // TODO real default values
    fillStyle= ""

    radius = 3

    glyph = None

    strokeStyle = ""
    strokeWidth = 1

    val propertyCustomizations: collection.Seq[PropertyCustomization] = new ListBuffer[PropertyCustomization]()

}
