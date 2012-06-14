package cz.payola.domain.rdf.ontology

/** A class that represents an ontology class.
  *
  * @param URI URI of the class.
  * @param label Label.
  * @param comment Comment.
  */
class Class(val URI: String, val label: String, val comment: String) extends cz.payola.common.rdf.ontology.Class
{

    /** Need to define these as private and create setters and getters.
      *
      * The classes don't necessarily have to be in a tree structure and may theoretically
      * form cycles -> we can't be sure which classes are going to be needed,
      * hence we can't pass the sequence in the constructor - the OntologyFactory
      * would end up in an infinite cycle.
      *
      */

    // Properties
    private var _properties: collection.Seq[Property] = Nil
    // Superclasses
    private var _superclasses: collection.Seq[Class] = Nil

    def containsPropertyWithURI(uri: String): Boolean = {
        _properties find { p: Property => p.URI == uri } isDefined
    }


    /** List of properties.
      *
      * @return List of properties.
      */
    def properties: Seq[Property] = _properties

    /** Set a list of properties.
      *
      * @param props List of properties.
      */
    private[ontology] def properties_=(props: Seq[Property]) {
        _properties = props
    }

    /** List of superclasses.
      *
      * @return List of superclasses.
      */
    def superclasses: Seq[Class] = _superclasses

    /** Set a list of superclasses.
      *
      * @param classes List of superclasses.
      */
    private[ontology] def superclasses_=(classes: Seq[Class]) {
        _superclasses = classes
    }

    /** Overriding toString method so that println prints detailed class information.
      *
      * @return Object description.
      */
    override def toString: String = {
        super.toString + " {\n\t" + label + " (" + comment + ")\n\tURI: " + URI + "\n\tSuperclasses: " + _superclasses.toString + "\n\tProperties: " + _properties.toString + "\n}"
    }
}
