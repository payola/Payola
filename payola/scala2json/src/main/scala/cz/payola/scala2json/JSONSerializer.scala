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

    val JSONSerializerOptionCondensedPrinting = 0 << 0
    val JSONSerializerOptionPrettyPrinting = 1 << 0
    val JSONSerializerOptionIgnoreNullValues = 1 << 1

    val JSONSerializerDefaultOptions = (JSONSerializerOptionCondensedPrinting |
                                                                 JSONSerializerOptionIgnoreNullValues)
}

import annotations.JSONFieldName
import JSONSerializerOptions._
import cz.payola.scala2json.annotations.JSONTransient

import scala.collection.mutable.StringBuilder
import java.lang.reflect.Field

// TODO: Keep track of already serialized objects to avoid cycles
// TODO: Pretty printing
// TODO: Name annotation name validation

class JSONSerializer(val obj: Any, options: Int = JSONSerializerDefaultOptions) {

    val prettyPrint: Boolean = (options & JSONSerializerOptionPrettyPrinting) != 0
    val ignoreNullValues: Boolean = (options & JSONSerializerOptionIgnoreNullValues) != 0

    private def _appendFieldToStringBuilder(f: Field, builder: StringBuilder, isFirst: Boolean): Boolean = {
        f.setAccessible(true)

        if (_isFieldTransient(f)){
            false
        }else{
            val fieldName = _nameOfField(f)
            val fieldValue = f.get(obj.asInstanceOf[AnyRef])
            if (fieldValue == null && ignoreNullValues){
                false
            }else{
                if (!isFirst){
                    builder.append(',')
                    if (prettyPrint){
                        builder.append('\n')
                    }
                }
                
                if (prettyPrint){
                    builder.append('\t')
                }
                
                builder.append(fieldName)
                builder.append(':')
                if (prettyPrint){
                    builder.append(' ')
                }

                val serializer: JSONSerializer =  new JSONSerializer(fieldValue, options)
                builder.append(serializer.stringValue)
                true
            }
        }
    }
    
    
    /** Returns whether @f has a JSONTransient annotation.
     *
     * @param f The field.
     *
     * @return True or false.
     */
    private def _isFieldTransient(f: Field): Boolean = {
        f.getAnnotation(classOf[JSONTransient]) != null
    }

    /** Returns the field's name - if it has a JSONFieldName annotation,
     *  it uses that.
     *
     *  @param f The field.
     *
     *  @return The field's name, considering annotations.
     */
    private def _nameOfField(f: Field): String = {
        val nameAnot = f.getAnnotation(classOf[JSONFieldName])
        if (nameAnot == null)
            f.getName
        else
            nameAnot.name // TODO name validation
    }

    /** Serializes an Array[_]
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeArray: String = {
        val builder: StringBuilder = new StringBuilder("[")

        // We know it is an Array[_]
        val arr: Array[_] = obj.asInstanceOf[Array[_]]

        for (i: Int <- 0 until arr.length) {
            if (i != 0)
                builder.append(',')

            val serializer: JSONSerializer =  new JSONSerializer(arr(i), options)
            builder.append(serializer.stringValue)
        }

        builder.append(']')
        builder.toString
    }

    /** Serializes an "array" - i.e. an object that implements
     *  the Iterable trait, yet isn't a map.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeIterable: String = {
        val builder: StringBuilder = new StringBuilder("[")

        // We know it is Iterable
        val coll: Iterable[_] = obj.asInstanceOf[Iterable[_]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        coll foreach { item => {
            if (index != 0)
                builder.append(',')
            index += 1

            val serializer: JSONSerializer =  new JSONSerializer(item, options)
            builder.append(serializer.stringValue)
        }}

        builder.append(']')
        builder.toString
    }
    

    /** Serializes an object that implements
     *  the Map trait, yet isn't a map.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeMap: String = {
        val builder: StringBuilder = new StringBuilder("{")

        // We know it is a Map
        val map: Map[_,_] = obj.asInstanceOf[Map[_,_]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        map foreach {case (key, value) => {
            if (index != 0)
                builder.append(',')
            index += 1

            val keySerializer: JSONSerializer =  new JSONSerializer(key, options)
            val valueSerializer: JSONSerializer =  new JSONSerializer(value, options)
            builder.append(keySerializer.stringValue)
            builder.append(":")
            builder.append(valueSerializer.stringValue)
        }}

        builder.append('}')
        builder.toString
    }

    /** Matches the @obj's type and calls the appropriate method.
     *
     * @param options See stringValue
     *
     * @return JSON representation of obj.
     */
    private def _serializeObject: String = {
        // *** Map is Iterable as well, but we shouldn't make it an array of
        // one-member dictionaries, rather make it a dictionary as a whole.
        // This is why Map **needs** to be matched first before Iterable.
        obj match {
            case _: String => JSONUtilities.escapedString(obj.asInstanceOf[String])
            case _: java.lang.Number => obj.toString
            case _: java.lang.Boolean => if (obj.asInstanceOf[java.lang.Boolean].booleanValue) "true"
                                         else "false"
            case _: java.lang.Character => JSONUtilities.escapedChar(obj.asInstanceOf[java.lang.Character].charValue())
            case _: Map[_,_] => _serializeMap
            case _: Iterable[_] => _serializeIterable
            case _: Array[_] => _serializeArray
            case _: AnyRef => _serializePlainObject
            case _ => _serializePrimitiveType
        }
    }

    /** Serializes an object - generally AnyRef 
      *
      * For most types, just calls obj.toString, the exception is
      * Boolean, which is converted to 'true' or 'false', Char is converted
      * to String. When Unit is encountred, an exception is raised.
      *
      * @return JSON value.
      */
    private def _serializePlainObject: String = {
        // Now we're dealing with some kind of an object,
        // we'll use Java's reflection to serialize it
        val builder: StringBuilder = new StringBuilder("{")

        if (prettyPrint)
            builder.append('\n')
            
        // Get object's fields:
        val c: Class[_] = obj.getClass
        val fields: Array[Field] = c.getDeclaredFields

        println("Got class " + c + " and " + fields.length)

        var haveProcessedField: Boolean = false
        for (i: Int <- 0 until fields.length) {
            if (_appendFieldToStringBuilder(fields(i), builder, i == 0)){
                haveProcessedField = true
            }
        }

        if (prettyPrint)
            builder.append('\n')
        
        builder.append('}')
        builder.toString
    }

    /** Serializes a primitive type.
      *
      * For most types, just calls obj.toString, the exception is
      * Boolean, which is converted to 'true' or 'false', Char is converted
      * to String. When Unit is encountred, an exception is raised.
      *
      * @return JSON value.
      */
    private def _serializePrimitiveType: String = {
        obj match {
            case _: Boolean => if (obj.asInstanceOf[Boolean]) "true" else "false"
            case _: Char => JSONUtilities.escapedChar(obj.asInstanceOf[Char])
            case _: Unit => throw new JSONSerializationException("Cannot serialize Unit.")
            case _ => obj.toString
        }
    }

    /** Serializes @obj to a JSON string.
     *
     * @return JSON representation of obj.
     */
   def stringValue: String = {
       // If obj is null, return "null" - as defined at http://www.json.org/
       // We can't simply ignore it, even though the options would have us
       // ignore null values - this needs to be eliminated earlier in the chain
       if (obj == null){
           "null"
       }else{

           // We need to distinguish several cases:
           // a) obj is a collection -> create an array
           // b) obj is a hash map or similar -> create a dictionary
           // c) obj is a string -> just escape it
           // d) obj is a scala.lang.Array -> create an array
           // e) obj is a primitive type -> convert it
           // f) obj is a regular object -> use reflection to create a dictionary
           //
           // Also have in mind that we support a special value handling
           // if the object implements some of the abstract traits in package
           // cz.payola.scala2json.traits
           _serializeObject
       }
   }

}
