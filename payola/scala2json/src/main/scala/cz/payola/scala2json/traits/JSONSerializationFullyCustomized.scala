package cz.payola.scala2json.traits

/**
 * This trait allows you to simply return the JSON value. The JSONSerializer
 * won't even touch the class if it implements this trait.
 *
 * Note: There is a few rules to follow when customizing the class's serialization:
 * 1) JSONSerializationFullyCustomized has the highest priority. If the JSONSerializer
 *    matches this trait, it's done. Nothing else gets called.
 * 2) If you use JSONSerializationCustomFields, JSONSerializationAdditionalFields methods
 *    do *not* get called, even if the class implements this trait.
 */
trait JSONSerializationFullyCustomized {

    /** Should return JSON representation of the object.
     *
     * @param options Options for the serialization. @see JSONSerializerOptions
     *
     * @return JSON representation of the object.
     */
    def JSONValue(ctx: Any, options: Int): String

}
