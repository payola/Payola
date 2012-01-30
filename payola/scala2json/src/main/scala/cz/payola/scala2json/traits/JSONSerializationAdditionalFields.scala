package cz.payola.scala2json.traits

import scala.collection.mutable.Map

/**
 * Allows the class to define additional fields that it doesn't have.
 * This way you can easily make some fields transient and return some
 * other value for them.
 *
 * Notes:
 * A) The fields get appended at the end of the object.
 * B) There is a few rules to follow when customizing the class's serialization:
 *   1) JSONSerializationFullyCustomized has the highest priority. If the JSONSerializer
 *      matches this trait, it's done. Nothing else gets called.
 *   2) If you use JSONSerializationCustomFields, JSONSerializationAdditionalFields methods
 *      do *not* get called, even if the class implements this trait.
 */
trait JSONSerializationAdditionalFields {

    /** Return any additional fields in a map. The keys, of course, must be
     *  Strings and mustn't contain spaces or any other special characters -
     *  just as if they were actual field names.
     *
     *  @return Map with the fields.
     */
    def additionalFieldsForJSONSerialization: Map[String, Any]

}
