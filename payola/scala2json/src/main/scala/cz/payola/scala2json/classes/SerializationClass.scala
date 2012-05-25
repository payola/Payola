package cz.payola.scala2json.classes

/** An abstract class that defines a Class[_] container.
  *
  */
abstract class SerializationClass
{
    def isClassOf(anObject: Any): Boolean
}
