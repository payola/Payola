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

import cz.payola.scala2json.annotations._
import cz.payola.scala2json.traits._
import JSONSerializerOptions._

import java.lang.reflect.Field
import scala.collection.mutable._


class JSONSerializer(val obj: Any, val options: Int = JSONSerializerDefaultOptions,
                            val processedObjects: ArrayBuffer[Any] = new ArrayBuffer[Any]()) {

    val prettyPrint: Boolean = (options & JSONSerializerOptionPrettyPrinting) != 0
    val ignoreNullValues: Boolean = (options & JSONSerializerOptionIgnoreNullValues) != 0

    // Cycle detection
    if (obj != null){
        if (processedObjects.contains(obj))
            throw new JSONSerializationException("Cycle detected on object " + obj + ".")
        else
            // Do not detect cycles on primitive types
            if (obj.isInstanceOf[AnyRef]
            && !obj.isInstanceOf[String]
            && !obj.isInstanceOf[java.lang.Number]
            && !obj.isInstanceOf[java.lang.Boolean]
            && !obj.isInstanceOf[java.lang.Character])
                processedObjects += obj
    }

    /** Appends an array item to string builder.
      *
      * @param item The item.
      * @param builder The builder
      * @param isFirst Whether the field is first - if it is, the comma separator is left out.
      *
      * @return False if the field has been skipped.
      */
    private def _appendStringArrayItemToStringBuilder(item: String, builder: StringBuilder, isFirst: Boolean) = {
        if (!isFirst){
            builder.append(',')
            if (prettyPrint){
                builder.append('\n')
            }
        }

        if (prettyPrint){
            builder.append('\t')
        }

        builder.append(item)
    }
    private def _appendArrayItemToStringBuilder(item: Any, builder: StringBuilder, isFirst: Boolean) = {
        val serializer: JSONSerializer =  new JSONSerializer(item, options, processedObjects.clone)
        var serializedObj: String = serializer.stringValue
        if (prettyPrint)
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        _appendStringArrayItemToStringBuilder(serializedObj, builder, isFirst)
    }

    /** Appends a field to string builder.
      *
      * @param f The field
      * @param builder The builder
      * @param isFirst Whether the field is first - if it is, the comma separator is left out.
      *
      * @return False if the field has been skipped.
      */
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
                _appendKeyValueToStringBuilder(fieldName, fieldValue, builder, isFirst)
                true
            }
        }
    }

    /** Appends "key: value" to @builder, preceded by comma, unless @isFirst is true.
      *
      * @param key The key.
      * @param value The value.
      * @param builder The builder.
      * @param isFirst If true, comma preceding the field is left out.
      *
      */
    private def _appendKeyValueToStringBuilder(key: String, value: Any,  builder: StringBuilder, isFirst: Boolean) = {
        var separator: String = ":"
        if (prettyPrint)
            separator = ": "

        val serializer: JSONSerializer =  new JSONSerializer(value, options, processedObjects.clone)
        var serializedObj: String = serializer.stringValue
        if (prettyPrint)
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        _appendStringArrayItemToStringBuilder("\"" + key + "\"" + separator + serializedObj, builder, isFirst)
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
            nameAnot.name
    }

    /** Returns the object's class name. Takes into consideration JSONUnnamedClass
      *  and JSONPoseableClass annotations.
      *
      * @return The object's class name or null if the object's class is annotated
      *                 with JSONUnnamedClass.
      */
    private def _objectsClassName: String = {
        val cl: Class[_] = obj.getClass
        if (cl.getAnnotation(classOf[JSONUnnamedClass]) != null){
            null
        }else if (cl.getAnnotation(classOf[JSONPoseableClass]) != null){
            val nameAnot: JSONPoseableClass = cl.getAnnotation(classOf[JSONPoseableClass])
            nameAnot.otherClassName
        }else{
            cl.getCanonicalName
        }
    }

    /** Serializes an Array[_]
     *
     * @return JSON representation of obj.
     */
    private def _serializeArray: String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (prettyPrint)
            builder.append('\n')
        
        // We know it is an Array[_]
        val arr: Array[_] = obj.asInstanceOf[Array[_]]

        for (i: Int <- 0 until arr.length) {
            _appendArrayItemToStringBuilder(arr(i), builder, i == 0)
        }

        if (prettyPrint)
            builder.append('\n')
        
        builder.append(']')
        builder.toString
    }

    /** Serializes an object that implements JSONSerializationCustomFields trait.
     *
     * @return JSON representation of obj.
     */
    private def _serializeCustomObject: String = {
        val builder: StringBuilder = new StringBuilder("{")
        if (prettyPrint)
            builder.append('\n')

        // We know it implements JSONSerializationCustomFields
        val customObj: JSONSerializationCustomFields = obj.asInstanceOf[JSONSerializationCustomFields]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        customObj.fieldNamesForJSONSerialization foreach { key => {
            _appendKeyValueToStringBuilder(key, customObj.fieldValueForKey(key), builder, index == 0)
            index += 1
        }}

        if (prettyPrint)
            builder.append('\n')

        builder.append('}')
        builder.toString
    }

    /** Serializes an "array" - i.e. an object that implements
     *  the Iterable trait, yet isn't a map.
     *
     * @return JSON representation of obj.
     */
    private def _serializeIterable: String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (prettyPrint)
            builder.append('\n')
        
        // We know it is Iterable
        val coll: Iterable[_] = obj.asInstanceOf[Iterable[_]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        coll foreach { item => {
            _appendArrayItemToStringBuilder(item, builder, index == 0)
            index += 1
        }}

        if (prettyPrint)
            builder.append('\n')
            
        builder.append(']')
        builder.toString
    }
    

    /** Serializes an object that implements
     *  the Map trait, yet isn't a map.
     *
     * @return JSON representation of obj.
     */
    private def _serializeMap: String = {
        val builder: StringBuilder = new StringBuilder("{")
        if (prettyPrint)
            builder.append('\n')
        
        // We know it is a Map[String, _]
        val map: Iterable[(String, _)] = obj.asInstanceOf[Iterable[(String, _)]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        map foreach {case (key, value) => {
            _appendKeyValueToStringBuilder(key, value, builder, index == 0)
            index += 1
        }}

        if (prettyPrint)
            builder.append('\n')
        
        builder.append('}')
        builder.toString
    }

    /** Matches the @obj's type and calls the appropriate method.
     *
     * @return JSON representation of obj.
     */
    private def _serializeObject: String = {
        // *** Map is Iterable as well, but we shouldn't make it an array of
        // one-member dictionaries, rather make it a dictionary as a whole.
        // This is why Map **needs** to be matched first before Iterable.
        obj match {
            case fullyCustomized: JSONSerializationFullyCustomized => fullyCustomized.JSONValue(options)
            case _: JSONSerializationCustomFields => _serializeCustomObject
            case _: String => JSONUtilities.escapedString(obj.asInstanceOf[String])
            case _: java.lang.Number => obj.toString
            case _: java.lang.Boolean => if (obj.asInstanceOf[java.lang.Boolean].booleanValue) "true"
                                         else "false"
            case _: java.lang.Character => JSONUtilities.escapedChar(obj.asInstanceOf[java.lang.Character].charValue())
            case _: scala.collection.immutable.Map[String, _] => _serializeMap
            case _: scala.collection.mutable.Map[String, _] => _serializeMap
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

        var haveProcessedField: Boolean = false
        
        val className: String = _objectsClassName
        if (className != null) {
            _appendKeyValueToStringBuilder("__class__", className, builder, true)
            haveProcessedField = true
        }
        
        for (i: Int <- 0 until fields.length) {
            if (_appendFieldToStringBuilder(fields(i), builder, !haveProcessedField)){
                haveProcessedField = true
            }
        }

        if (obj.isInstanceOf[JSONSerializationAdditionalFields]){
            // Additional fields for the object
            val map: Map[String, Any]
                    = obj.asInstanceOf[JSONSerializationAdditionalFields].additionalFieldsForJSONSerialization
            if (map.size != 0)
                if (fields.size != 0)
                    builder.append(',')
                builder.append('\n')

            var index: Int = 0
            map foreach {case (key, value) => {
                _appendKeyValueToStringBuilder(key, value, builder, index == 0)
                index += 1
            }}

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
      * to String. When Unit is encountered, an exception is raised.
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
