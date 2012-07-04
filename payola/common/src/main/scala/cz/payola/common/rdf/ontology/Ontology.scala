package cz.payola.common.rdf.ontology

import scala.collection._

/**
  * A description of a set of object classes and relationships between them (http://www.w3.org/TR/owl-features/).
  * @param classes The classes in the ontology indexed by their URIs.
  * @param superClasses The super class relation between the classes in the ontology.
  */
class Ontology(val classes: immutable.Map[String, Class],
    val superClasses: immutable.Map[String, immutable.Seq[String]])
{
    /**
      * Merges this ontology with the other one into a new one.
      * @param otherOntology The other ontology.
      * @return New instance with merged classes and properties.
      */
    def +(otherOntology: Ontology): Ontology = {
        val mergedClasses = mutable.HashMap.empty[String, Class]
        val mergedSuperClasses = mutable.HashMap.empty[String, Seq[String]]

        List(this, otherOntology).foreach { ontology =>
            ontology.classes.foreach { c =>
                val merged = mergedClasses.remove(c._1).map(_ + c._2)
                mergedClasses.put(c._1, merged.getOrElse(c._2))
            }
            ontology.superClasses.foreach { s =>
                val merged = mergedSuperClasses.remove(s._1).map(_ ++ s._2)
                mergedSuperClasses.put(s._1, merged.getOrElse(s._2))
            }
        }

        new Ontology(mergedClasses.toMap, mergedSuperClasses.mapValues(_.toList).toMap)
    }
}
