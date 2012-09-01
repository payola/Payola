package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities._

/**
 * This object converts [[cz.payola.domain.entities.settings.PropertyCustomization]] to
 * [[cz.payola.data.squeryl.entities.settings.PropertyCustomization]]
 */
object PropertyCustomization extends EntityConverter[PropertyCustomization]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[PropertyCustomization] = {
        entity match {
            case p: PropertyCustomization => Some(p)
            case p: cz.payola.common.entities.settings.PropertyCustomization
            => Some(new PropertyCustomization(p.id, p.uri, p.strokeColor, p.strokeWidth))
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.settings.PropertyCustomization]] entities.
 * @param id ID of the property customization
 * @param uri URI of the property customization
 * @param strokeColor Stroke color of the property customization
 * @param strokeWidth Stroke width of the property customization
 * @param context Implicit context
 */
class PropertyCustomization(
    override val id: String, uri: String, strokeColor: String, strokeWidth: Int)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.PropertyCustomization(uri, strokeColor, strokeWidth)
    with Entity
{
    var classCustomizationId: String = null
}
