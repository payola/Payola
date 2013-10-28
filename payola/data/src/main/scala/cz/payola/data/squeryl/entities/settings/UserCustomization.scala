package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl.entities._
import scala.collection.immutable
import cz.payola.data.squeryl._

/**
 * This object converts [[cz.payola.domain.entities.settings.UserCustomization]] to
 * [[cz.payola.data.squeryl.entities.settings.UserCustomization]]
 */
object UserCustomization extends EntityConverter[UserCustomization]
{
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[UserCustomization] = {
        entity match {
            case o: UserCustomization => Some(o)
            case o: cz.payola.common.entities.settings.UserCustomization => {
                val customizations = o.classCustomizations.map(ClassCustomization(_))
                Some(new UserCustomization(o.id, o.URLs, o.name, o.owner.map(User(_)), customizations, o.isPublic))
            }
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.settings.OntologyCustomization]] entities.
 * @param id ID of the ontology customization
 * @param u Coma separated list of URLs of the ontology customization
 * @param n Name of the ontology customization
 * @param o Owner of the ontology customization
 * @param c List of child class customizations
 * @param _isPub Whether the ontology customization is public or not
 * @param context Implicit context
 */
class UserCustomization(
    override val id: String, u: String, n: String, o: Option[User],
    c: immutable.Seq[ClassCustomization], var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.UserCustomization(id, u, n, o, c)
    with Entity with OptionallyOwnedEntity with ShareableEntity
{
    def classCustomizations_=(value: immutable.Seq[ClassCustomizationType]) {
        _classCustomizations = value
    }
}