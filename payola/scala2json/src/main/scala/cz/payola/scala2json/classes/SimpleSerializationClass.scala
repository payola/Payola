package cz.payola.scala2json.classes

/**
  * Represents class of non-generic objects (User, Group...)
  * @param objectClass Class of the object.
  */
case class SimpleSerializationClass(objectClass: Class[_]) extends SerializationClass
{
    def isClassOf(anObject: Any): Boolean = {
        objectClass.isInstance(anObject)
    }
}
