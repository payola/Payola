package cz.payola.domain.entities.settings.ontology

import cz.payola.domain.Entity

/** This class represents ontology property visual customization.
  */
class PropertyCustomization(val URI: String) extends Entity with cz.payola.common.entities.settings.ontology.PropertyCustomization
{
    // TODO real defaults
    strokeStyle = ""
    strokeWidth = 1

    straightenFactor = 1
}
