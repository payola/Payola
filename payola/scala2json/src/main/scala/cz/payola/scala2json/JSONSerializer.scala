package cz.payola.scala2json

object JSONSerializerOptions {
    /** Default options are condensed printing.
     *
     * Condensed printing removes all unnecessary white space, which results in
     * smaller data, but is not well human-readable.
     *
     * Pretty printing option adds tabs to make the output readable, which can
     * be used for debugging.
     *
     * No other options available currently.
     */

    val JSONSerializerDefaultOptions = JSONSerializerOptionCondensedPrinting
    val JSONSerializerOptionCondensedPrinting = 1 << 0
    val JSONSerializerOptionPrettyPrinting = 1 << 1
}

import JSONSerializerOptions._
import scala.collection.mutable.StringBuilder

// TODO: Keep track of already serialized objects to avoid cycles

class JSONSerializer(val obj: Any) {

    /** Serializes an Array[_]
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeArray(options: Int): String = {
        val builder: StringBuilder = new StringBuilder("[")

        // We know it is an Array[_]
        val arr: Array[_] = obj.asInstanceOf[Array[_]]

        for (i: Int <- 0 until  arr.length) {
            if (i != 0)
                builder.append(",")

            val serializer: JSONSerializer =  new JSONSerializer(arr(i))
            builder.append(serializer.stringValue(options))
        }

        builder.append("]")
        builder.toString
    }

    /** Serializes an "array" - i.e. an object that implements
     *  the Iterable trait, yet isn't a map.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeIterable(options: Int): String = {
        val builder: StringBuilder = new StringBuilder("[")

        // We know it is Iterable
        val coll: Iterable[_] = obj.asInstanceOf[Iterable[_]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        coll foreach { item => {
            if (index != 0)
                builder.append(",")
            index += 1

            val serializer: JSONSerializer =  new JSONSerializer(item)
            builder.append(serializer.stringValue(options))
        }}

        builder.append("]")
        builder.toString
    }

    /** Serializes an object that implements
     *  the Map trait, yet isn't a map.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeMap(options: Int): String = {
        val builder: StringBuilder = new StringBuilder("{")

        // We know it is a Map
        val map: Map[_,_] = obj.asInstanceOf[Map[_,_]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        map foreach {case (key, value) => {
            if (index != 0)
                builder.append(",")
            index += 1

            val keySerializer: JSONSerializer =  new JSONSerializer(key)
            val valueSerializer: JSONSerializer =  new JSONSerializer(value)
            builder.append(keySerializer.stringValue(options))
            builder.append(":")
            builder.append(valueSerializer.stringValue(options))
        }}

        builder.append("}")
        builder.toString
    }

    /** Matches the @obj's type and calls the appropriate method.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeObject(options: Int): String = {
        // *** Map is Iterable as well, but we shouldn't make it an array of
        // one-member dictionaries, rather make it a dictionary as a whole.
        // This is why Map **needs** to be matched first before Iterable.
        obj match {
            case _: Map[_,_] => _serializeMap(options)
            case _: Iterable[_] => _serializeIterable(options)
            case _: Array[_] => _serializeArray(options)
            case _: AnyRef => _serializePlainObject(options)
            case _ => _serializePrimitiveType(options)
        }
    }

    private def _serializePlainObject(options: Int): String = {
        ""
    }


    private def _serializePrimitiveType(options: Int): String = {
        obj match {
            case _: Boolean => if (obj.asInstanceOf[Boolean]) "true" else "false"
            case _: Unit => throw new JSONSerializationException("Cannot serialize Unit.")
            case _ => obj.toString
        }
    }

    /** Serializes @obj to a JSON string.
     *
     * @param options OR'ed options defined by enum JSONSerializerOptions.
     *
     * @return JSON representation of obj.
     */
   def stringValue(options: Int = JSONSerializerDefaultOptions): String = {
       // If obj is null, return an empty array
       if (obj == null){
           "[]"
       }

       // We need to distinguish several cases:
       // a) obj is a collection -> create an array
       // b) obj is a hash map or similar -> create a dictionary
       // c) obj is a regular object -> use reflection to create a dictionary
       //
       // Also have in mind that we support a special value handling
       // if the object implements some of the abstract traits in package
       // cz.payola.scala2json.traits
       _serializeObject(options)
   }

}
