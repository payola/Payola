package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl.entities._
import scala.collection.immutable
import cz.payola.data.squeryl._

/**
 * This object converts [[cz.payola.domain.entities.settings.Customization]] to
 * [[cz.payola.data.squeryl.entities.settings.Customization]]
 */
object Customization extends EntityConverter[Customization]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[Customization] = {

        entity match {
            case o: Some[_] =>
                o.get match {
                    case c: Customization =>
                        Some(c)
                    case _ =>
                        None
                }
            case o: Customization => Some(o)
            case o: cz.payola.common.entities.settings.Customization => {
                val customizations = o.classCustomizations.map(ClassCustomization(_))
                Some(new Customization(
                    o.id, Some(o.isUserDefined), o.URLs, o.name, o.owner.map(User(_)),
                    customizations, o.isPublic))
            }
            case o: cz.payola.common.entities.settings.UserCustomization => {

                val customizations = o.classCustomizations.map(ClassCustomization(_))

                val res = new Customization(
                    o.id, Some(true), o.URLs, o.name, o.owner.map(User(_)), customizations, o.isPublic)
                Some(res)
            }
            case o: cz.payola.common.entities.settings.OntologyCustomization => {
                val customizations = o.classCustomizations.map(ClassCustomization(_))
                val res = new Customization(o.id, Some(false), o.URLs, o.name, o.owner.map(User(_)),
                    customizations, o.isPublic)
                Some(res)
            }
            case _ =>
                None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.settings.OntologyCustomization]] entities.
 * @param id ID of the ontology customization
 * @param u Coma separated list of URLs of the ontology customization
 * @param n Name of the customization
 * @param o Owner of the customization
 * @param c List of child class customizations
 * @param _isPub Whether the customization is public or not
 * @param context Implicit context
 */
class Customization(
    override val id: String, uDef: Option[Boolean], u: String, n: String, o: Option[User],
    c: immutable.Seq[ClassCustomization], var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.Customization(uDef, u, n, o, c)
    with Entity with OptionallyOwnedEntity with ShareableEntity
{
    def classCustomizations_=(value: immutable.Seq[ClassCustomizationType]) {
        _classCustomizations = value
    }
}
