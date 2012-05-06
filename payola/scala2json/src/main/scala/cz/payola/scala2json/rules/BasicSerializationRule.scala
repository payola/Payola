package cz.payola.scala2json.rules

/** BasicSerializationRule allows you to modify JSONSerializer's behavior. You can
  * define a class or a trait that should be used for obtaining a field list, list of fields to
  * skip and a map of aliases - to rename the fields.
  *
  * @param serializeAsClass The class that should be used to obtain the field names from.
  * @param transientFields The fields that are skipped during the serialization.
  * @param fieldAliases Allows fields to use different names.
  */
case class BasicSerializationRule(
    serializeAsClass: Option[Class[_]] = None,
    transientFields: Option[collection.Seq[String]] = None,
    fieldAliases: Option[collection.Map[String, String]] = None) extends SerializationRule
