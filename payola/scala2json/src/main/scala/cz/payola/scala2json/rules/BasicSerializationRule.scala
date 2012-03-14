package cz.payola.scala2json.rules

/**
  *
  * @param serializeAsClass The class that should be used to obtain the field names from.
  * @param transientFields The fields that are skipped during the serialization.
  * @param fieldAliases Allows fields to use different names.
  */
case class BasicSerializationRule(
    serializeAsClass: Option[Class[_]] = None,
    transientFields: Option[collection.Seq[String]] = None,
    fieldAliases: Option[collection.Map[String, String]] = None
    ) extends SerializationRule
