package cz.payola.domain.entities.settings

import cz.payola.domain._
import cz.payola.domain.entities._
import scala.collection.immutable
import cz.payola.domain.rdf.ontology.Ontology
import cz.payola.domain.net.Downloader
import cz.payola.common.ValidationException

object OntologyCustomization
{
    /**
      * Crates an empty ontology customization for the specified ontology.
      * @param ontologyURLs URLs of ontologies.
      * @param name Name of the customization.
      * @param owner Owner of the customization.
      * @return The customization.
      */
    def empty(ontologyURLs: Seq[String], name: String, owner: Option[User]): OntologyCustomization = {
        try {
             val classCustomizations = ontologyURLs.map{ url =>
                 val ontology = Ontology(new Downloader(url, accept = "application/rdf+xml").result)


                 ontology.classes.values.map { c =>
                     val propertyCustomizations = c.properties.values.map { p =>
                         new PropertyCustomization(p.uri, "", 0)
                     }
                     new ClassCustomization(c.uri, "", 0, "", propertyCustomizations.toList)
                 }
             }.flatten

             new OntologyCustomization(ontologyURLs.mkString(","), name, owner, classCustomizations.toList)
        } catch {
            case _ => throw new ValidationException("ontologyURL", "Couldn't fetch an ontology from one of the specified URLs.")
        }
    }

    /**
     * Crates an empty ontology customization for the specified ontology.
     * @param name Name of the customization.
     * @param owner Owner of the customization.
     * @return The customization.
     */
    def userDefined(name: String, owner: Option[User]): OntologyCustomization = {

        new OntologyCustomization(
            "http://user_"+owner.get.id+"/"+owner.get.name, name, owner, List[ClassCustomization]())
    }
}

class OntologyCustomization(
    val ontologyURLs: String,
    protected var _name: String,
    protected var _owner: Option[User],
    protected var _classCustomizations: immutable.Seq[ClassCustomization])
    extends Entity
    with NamedEntity
    with OptionallyOwnedEntity
    with cz.payola.common.entities.settings.OntologyCustomization
{
    checkConstructorPostConditions()

    type ClassCustomizationType = ClassCustomization

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[OntologyCustomization]
    }

    def appendClassCustomization(classCust: ClassCustomization) {
        _classCustomizations = _classCustomizations ++ Seq(classCust)
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        super[NamedEntity].checkInvariants()
        super[OptionallyOwnedEntity].checkInvariants()
    }
}
