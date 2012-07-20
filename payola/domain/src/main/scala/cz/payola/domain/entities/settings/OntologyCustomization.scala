package cz.payola.domain.entities.settings

import cz.payola.domain._
import cz.payola.domain.entities._
import scala.collection.immutable
import cz.payola.domain.rdf.ontology.Ontology
import cz.payola.domain.net.Downloader
import org.apache.xerces.impl.dv.DatatypeException
import cz.payola.common.ValidationException

object OntologyCustomization
{
    /**
      * Crates an empty ontology customization for the specified ontology.
      * @param ontologyURL URL of the ontology.
      * @param name Name of the customization.
      * @param owner Owner of the customization.
      * @return The customization.
      */
    def empty(ontologyURL: String, name: String, owner: Option[User]): OntologyCustomization = {
        val ontology = try {
             Ontology(new Downloader(ontologyURL, accept = "application/rdf+xml").result)
        } catch {
            case _ => throw new ValidationException("ontologyURL", "Couldn't fetch an ontology from the specified URL.")
        }

        val classCustomizations =ontology.classes.values.map { c =>
            val propertyCustomizations = c.properties.values.map { p =>
                new PropertyCustomization(p.uri, "", 0)
            }
            new ClassCustomization(c.uri, "", 0, None, propertyCustomizations.toList)
        }

        new OntologyCustomization(ontologyURL, name, owner, classCustomizations.toList)
    }
}

class OntologyCustomization(
    val ontologyURL: String,
    protected var _name: String,
    protected var _owner: Option[User],
    protected var _classCustomizations: immutable.Seq[ClassCustomization])
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with ShareableEntity
    with cz.payola.common.entities.settings.OntologyCustomization
{
    checkConstructorPostConditions()

    type ClassCustomizationType = ClassCustomization

    def entityTypeName = "ontology customization"

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[OntologyCustomization]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
        super[ShareableEntity].checkInvariants()
    }
}
