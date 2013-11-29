package cz.payola.data.squeryl.entities.settings

import scala.collection.immutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl._

/**
 * This object converts [[cz.payola.domain.entities.settings.ClassCustomization]] to
 * [[cz.payola.data.squeryl.entities.settings.ClassCustomization]]
 */
object ClassCustomization extends EntityConverter[ClassCustomization]
{

    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[ClassCustomization] = {
        entity match {
            case c: ClassCustomization => Some(c)
            case c: cz.payola.common.entities.settings.ClassCustomization => {
                val customizations = c.propertyCustomizations.map(PropertyCustomization(_))
                Some(new ClassCustomization(c.id, c.uri, c.fillColor, c.radius, c.glyph, c.labels, c.conditionalValue,
                    c.orderNumber, customizations))
            }
            case c: Some[_] =>
                c.get match {
                    case d: ClassCustomization =>
                        Some(d) //in case, there is nothing to convert
                    case _ =>
                        None
                }
            case _ =>
                None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.settings.ClassCustomization]] entities.
 * @param id ID of the class customization
 * @param uri URI of the class customization
 * @param fillColor Fill color of the class customization
 * @param conditionalValue if this is conditional ClassCustomization this contains aditional specification to uri
 * @param radius Radius of the class customization
 * @param glyph Glyph of the class customization
 * @param labels List of labels representing the class
 * @param customizations List of child property customizations
 * @param context Implicit context
 */
class ClassCustomization(
    override val id: String, uri: String, fillColor: String, radius: Int,
    glyph: String, labels: String, conditionalValue: String, orderNum: Int, customizations: immutable.Seq[PropertyCustomization])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.ClassCustomization(
        uri, fillColor, radius, glyph, labels, conditionalValue, customizations, orderNum)
    with Entity
{
    var customizationId: String = null

    def propertyCustomizations_=(value: Seq[PropertyCustomizationType]) {
        _propertyCustomizations = value.toList
    }
}
