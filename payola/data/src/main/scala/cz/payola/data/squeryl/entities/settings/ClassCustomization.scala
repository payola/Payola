package cz.payola.data.squeryl.entities.settings

import cz.payola.domain.Entity
import scala.collection.immutable
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
    override val id: String, u: String, f: String, r: Int, g: Option[Char], c: immutable.Seq[PropertyCustomization])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.ClassCustomization(u, f, r, g, c)
    with PersistableEntity
{
    var ontologyCustomizationId: String = null

    private lazy val _customizationsQuery = context.schema.propertyCustomizationsOfClasses.left(this)
    
    def associatePropertyCustomization(customization: PropertyCustomizationType) = {
        context.schema.associate(PropertyCustomization(customization), _customizationsQuery)
    }
}
