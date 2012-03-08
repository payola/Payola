package cz.payola.scala2json.traits

import scala.collection.mutable.Iterable

/**
 * This trait allows the class to return custom field names and values.
 *
 * The main difference between this and the additional-fields trait is
 * that here you have a full control over the serialization, whereas with
 * the additional-fields trait, you can only define additional fields.
 *
 * Note: There is a few rules to follow when customizing the class's serialization:
 * 1) JSONSerializationFullyCustomized has the highest priority. If the JSONSerializer
 *    matches this trait, it's done. Nothing else gets called.
 * 2) If you use JSONSerializationCustomFields, JSONSerializationAdditionalFields methods
 *    do *not* get called, even if the class implements this trait.
 *
 */
trait JSONSerializationCustomFields {

    /** Return the names of the fields.
     *
     * @return Iterable collection for the field names.
     */
    def fieldNamesForJSONSerialization(ctx: Any): scala.collection.Iterable[String]

    /** Return the value for the field named @key.
     *
     * @param key Value for the field called @key.
     *
     * @return The value.
     */
    def fieldValueForKey(ctx: Any, key: String): Any
}
