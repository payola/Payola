package cz.payola.data.squeryl.entities.settings

import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.SquerylDataContextComponent


object ClassCustomization extends EntityConverter[ClassCustomization]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[ClassCustomization] = {
        entity match {
            case c: ClassCustomization => Some(c)
            case c: cz.payola.common.entities.settings.ClassCustomization => {
                val customizations = c.propertyCustomizations.map(PropertyCustomization(_))
                Some(new ClassCustomization(c.id, c.uri, c.fillColor, c.radius, c.glyph, customizations))
            }
            case _ => None
        }
    }
}

class ClassCustomization(
    override val id: String, uri: String, fillColor: String, radius: Int,
    glyph: String,customizations: immutable.Seq[PropertyCustomization])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.ClassCustomization(
        uri, fillColor, radius, glyph, customizations)
    with PersistableEntity
{
    var ontologyCustomizationId: String = null

    def propertyCustomizations_=(value: Seq[PropertyCustomizationType]) { _propertyCustomizations = value.toList}
}
