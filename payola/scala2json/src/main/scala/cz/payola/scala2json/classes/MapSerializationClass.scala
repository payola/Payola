package cz.payola.scala2json.classes

/**
  * Represents class of maps (Map[String, User], ListBuffer[Group])
  * @param mapClass Class of the map.
  * @param keySerializationClass Serialization class of the keys.
  * @param valueSerializationClass Serialization class of the values.
  */
case class MapSerializationClass(mapClass: Class[_], keySerializationClass: SerializationClass,
    valueSerializationClass: SerializationClass) extends SerializationClass
{
    def isClassOf(anObject: Any): Boolean = {
        // ??? Maybe also check whether for each key -> value:
        //     keySerializationClass.isClassOf(key)
        //     valueSerializationClass.isClassOf(value)
        mapClass.isInstance(anObject)
    }
}
