package cz.payola.scala2json

object OutputFormat extends Enumeration
{
    type OutputFormat = Value

    val PrettyPrinted = Value

    val Condensed = Value
}

trait Serializer
{
    var outputFormat = OutputFormat.PrettyPrinted
    
    /** Whether fields containing object class fully qualified names are included. */
    var includeClasses = true

    def addSerializationRule(forClass: SerializationClass, rule: SerializationRule)

    def serialize(anObject: Any): String
}




abstract class SerializationRule

/**
  *
  * @param serializeAsClass The class that should be used to obtain the field names from.
  * @param transientFields The fields that are skipped during the serialization.
  * @param fieldAliases Allows fields to use different names.
  */
case class BasicSerializationRule(
    serializeAsClass: Option[Class[_]] = None,
    transientFields: Option[Seq[String]] = None,
    fieldAliases: Option[Map[String, String]] = None
) extends SerializationRule

case class CustomSerializationRule(customSerializer: ((Serializer, Any) => String))




abstract class SerializationClass
{
    def isClassOf(anObject: Any): Boolean
}

/**
  * Represents class of non-generic objects (User, Group...)
  * @param objectClass Class of the object.
  */
case class SimpleSerializationClass(objectClass: Class[_])
{
    def isClassOf(anObject: Any): Boolean = {
        // anObject.getclass <: objectClass
        false
    }
}

/**
  * Represents class of sequences (List[User], ListBuffer[Group])
  * @param seqClass Class of the sequence.
  * @param itemSerializationClass Serialization class of the items.
  */
case class SeqSerializationClass(seqClass: Class[_], itemSerializationClass: SerializationClass)
{
    def isClassOf(anObject: Any): Boolean = {
        // anObject.getclass <: seqClass
        // ??? Maybe also check whether for each item: itemSerializationClass.isClassOf(item)
        false
    }
}

/**
  * Represents class of maps (Map[String, User], ListBuffer[Group])
  * @param mapClass Class of the map.
  * @param keySerializationClass Serialization class of the keys.
  * @param valueSerializationClass Serialization class of the values.
  */
case class MapSerializationClass(mapClass: Class[_], keySerializationClass: SerializationClass, valueSerializationClass: SerializationClass)
{
    def isClassOf(anObject: Any): Boolean = {
        // anObject.getclass <: seqClass
        // ??? Maybe also check whether for each key -> value: 
        //     keySerializationClass.isClassOf(key)
        //     valueSerializationClass.isClassOf(value)
        false
    }
}
