package cz.payola.domain.rdf.ontology


class Class(val URI: String, val label: String, val comment: String)
{
    private var _properties: collection.Seq[Property] = Nil
    private var _superclasses: collection.Seq[Class] = Nil

    /** Need to define these setters and getters and keep it var.
      *
      * The classes don't necessarily have to be in a tree structure and may
      * form cycles -> we can't be sure which classes are going to be needed,
      * hence we can't pass the sequence in the constructor.
      *
      * The same goes for properties as those are of some class as well.
      *
      * @return
      */

    def properties: Seq[Property] = _properties
    private[ontology] def properties_=(props: Seq[Property]) {
        _properties = props
    }

    def superclasses: Seq[Class] = _superclasses
    private[ontology] def superclasses_=(classes: Seq[Class]) {
        _superclasses = classes
    }

    override def toString: String = {
        super.toString + " {\n\t" + label + " (" + comment + ")\n\tURI: " + URI + "\n\tSuperclasses: " + _superclasses.toString + "\n\tProperties: " + _properties.toString + "\n}"
    }
}
