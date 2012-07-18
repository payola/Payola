package cz.payola.data.squeryl.entities.settings

import cz.payola.data.squeryl.entities._
import scala.collection.immutable
import cz.payola.data.squeryl.SquerylDataContextComponent

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

class OntologyCustomization(
    override val id: String, u: String, n: String, o: Option[User],
    c: immutable.Seq[ClassCustomization], var _isPub: Boolean)
    (implicit val context: SquerylDataContextComponent)
    extends cz.payola.domain.entities.settings.OntologyCustomization(u, n, o, c)
    with PersistableEntity with OptionallyOwnedEntity with ShareableEntity with NamedEntity
{
    _classCustomizations = null

    var analysisId: Option[String] = None

    override def classCustomizations: immutable.Seq[ClassCustomizationType] = {
        if (_classCustomizations == null) {
            _classCustomizations = wrapInTransaction {
                context.ontologyCustomizationRepository.getClassCustomizations(id).toList
            }
        }

        _classCustomizations.toList
    }
}
