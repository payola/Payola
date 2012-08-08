package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl.entities._
import scala.collection.immutable
import cz.payola.data.squeryl._
import scala.Some

/**
 * This object converts [[cz.payola.domain.entities.settings.PropertyCustomization]] to
 * [[cz.payola.data.squeryl.entities.settings.PropertyCustomization]]
 */
object OntologyCustomization extends EntityConverter[OntologyCustomization]
{    
    def convert(entity: AnyRef)(implicit context: SquerylDataContextComponent): Option[OntologyCustomization] = {
        entity match {
            case o: OntologyCustomization => Some(o)
            case o: cz.payola.common.entities.settings.OntologyCustomization => {
                val customizations = o.classCustomizations.map(ClassCustomization(_))
                Some(new OntologyCustomization(
                    o.id, o.ontologyURL, o.name, o.owner.map(User(_)), customizations, o.isPublic))
            }
            case _ => None
        }
    }
}

/**
 * Provides database persistence to [[cz.payola.domain.entities.settings.OntologyCustomization]] entities.
 * @param id ID of the ontology customization
 * @param u URI of the ontology customization
 * @param n Name of the ontology customization
 * @param o Owner of the ontology customization
 * @param c List of child class customizations
 * @param _isPub Whether the ontology customization is public or not
 * @param context Implicit context
 */
class OntologyCustomization(
    override val id: String, u: String, n: String, o: Option[User],
    c: immutable.Seq[ClassCustomization], var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.OntologyCustomization(u, n, o, c)
    with Entity with OptionallyOwnedEntity with ShareableEntity
{
    def classCustomizations_=(value: immutable.Seq[ClassCustomizationType]) { _classCustomizations = value }
}
