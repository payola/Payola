package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.data.squeryl.entities._

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

class PropertyCustomization(
    override val id: String, u: String, c: String, w: Int)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.PropertyCustomization(u, c, w)
    with PersistableEntity
{
    var classCustomizationId: String = null
}
