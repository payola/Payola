package cz.payola.data.squeryl.entities.settings

import scala.collection.immutable
import scala.collection.mutable
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.SquerylDataContextComponent
import org.squeryl.annotations.Column


object ClassCustomization extends EntityConverter[ClassCustomization]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[ClassCustomization] = {
        entity match {
            case c: ClassCustomization => Some(c)
            case c: cz.payola.common.entities.settings.ClassCustomization => {
                val customizations = c.propertyCustomizations.map(PropertyCustomization(_))
                Some(new ClassCustomization(c.id, c.uri, c.fillColor, c.radius, convertGlyph2Str(c.glyph), customizations))
            }
            case _ => None
        }
    }
    
    def convertGlyph2Char(g: Option[String]): Option[Char] = { g.map(_(0)) }
    
    def convertGlyph2Str(g: Option[Char]): Option[String] = { g.map(_.toString) }
}

class ClassCustomization(
    override val id: String, uri: String, fillColor: String, radius: Int,
    var _g: Option[String],customizations: immutable.Seq[PropertyCustomization])
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.ClassCustomization(
        uri, fillColor, radius, ClassCustomization.convertGlyph2Char(_g), customizations)
    with PersistableEntity
{
    var ontologyCustomizationId: String = null

    super.glyph = ClassCustomization.convertGlyph2Char(_g)
    
    def propertyCustomizations_=(value: Seq[PropertyCustomizationType]) { _propertyCustomizations = value.toList}

    override def glyph = ClassCustomization.convertGlyph2Char(_g)

    override def glyph_=(value: Option[Char]) {
        super.glyph = value

        _g = ClassCustomization.convertGlyph2Str(value)
    }
}
