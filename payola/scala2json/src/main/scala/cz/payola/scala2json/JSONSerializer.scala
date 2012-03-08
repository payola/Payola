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
     * Ignoring null values will skip fields that have a null value, otherwise 'null'
     * would be written to output.
     *
     * By default, JSONSerializer assigns all objects an object ID (as an __objectID__
     * field) in order to deal with cyclic dependencies and reducing the output size
     * by referencing objects it's already encountered. This may be an undesired
     * behavior, for example for the RDF graph serialization. Hence the skip object
     * IDs option.
     */

    val JSONSerializerOptionCondensedPrinting = 0 << 0
    val JSONSerializerOptionPrettyPrinting = 1 << 0
    val JSONSerializerOptionIgnoreNullValues = 1 << 1
    val JSONSerializerOptionSkipObjectIDs = 1 << 2
    val JSONSerializerOptionDisableCustomSerialization = 1 << 3

    val JSONSerializerDefaultOptions = JSONSerializerOptionCondensedPrinting
}

import cz.payola.scala2json.annotations._
import cz.payola.scala2json.traits._
import JSONSerializerOptions._

import java.lang.reflect.Field
import scala.collection.mutable._

@JSONUnnamedClass private class ConcreteArrayClassHolder {
    @JSONFieldName(name = "__arrayClass__") var arrayClass: String = null
    @JSONFieldName(name = "__value__") var value: scala.collection.Traversable[_] = null
}

class JSONSerializer(val obj: Any, val options: Int = JSONSerializerDefaultOptions,
                            val processedObjects: ArrayBuffer[Any] = new ArrayBuffer[Any]()) {
    
    private val prettyPrint: Boolean = (options & JSONSerializerOptionPrettyPrinting) != 0
    private val disableCustomSerialization = (options & JSONSerializerOptionDisableCustomSerialization) != 0
    private val ignoreNullValues: Boolean = (options & JSONSerializerOptionIgnoreNullValues) != 0
    private val skipObjectIDs: Boolean = (options & JSONSerializerOptionSkipObjectIDs) != 0

    // The object ID is set correctly below
    private var objectID: Int = 0

    // If this is a second encounter of the object, just serialize it as a reference
    private var serializeObjectAsReference: Boolean = false

    // A user-assignable field which allows easily to pass a context to all serialized objects.
    // As an example, the RDFGraph uses the context to pass a HashMap of shortened URIs
    // to vertices, edges, etc.
    var context: Any = null

    // Cycle detection
    if (obj != null){
        if (processedObjects.contains(obj)){
            if (skipObjectIDs){
                // Skipping IDs -> can't serialize a cycle
                throw new JSONSerializationException("Cycle detected on object " + obj + ".")
            }else{
                // Otherwise, fetch the objectID and serialize it as a reference
                objectID = processedObjects.indexOf(obj)
                serializeObjectAsReference = true
            }
        }else if (obj.isInstanceOf[AnyRef]
            && !obj.isInstanceOf[String]
            && !obj.isInstanceOf[java.lang.Number]
            && !obj.isInstanceOf[java.lang.Boolean]
            && !obj.isInstanceOf[java.lang.Character]){
            // Do not detect cycles on primitive types
            objectID = processedObjects.size // The index will be at the size of the array
            processedObjects += obj
        }
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
        var serializer: JSONSerializer = null
        if (skipObjectIDs){
            // If skipping object IDs, need to clone the array, otherwise we could detect
            // a cycle that's not really there
            serializer = new JSONSerializer(item, options, processedObjects.clone)
            serializer.context = context
        }else{
            // Do not clone the processedObjects as it will save space
            serializer = new JSONSerializer(item, options, processedObjects)
            serializer.context = context
        }


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
                val isConcreteArrayClass = _isConcreteArrayClassField(f)
                val isTraversable = fieldValue.isInstanceOf[scala.collection.Traversable[_]]

                if (isConcreteArrayClass && isTraversable){
                    val holder = new ConcreteArrayClassHolder()
                    holder.arrayClass = f.getAnnotation(classOf[JSONConcreteArrayClass]).arrayClass.getName
                    holder.value = fieldValue.asInstanceOf[scala.collection.Traversable[_]]
                    val serializer = new JSONSerializer(holder, options, processedObjects)
                    serializer.context = context
                    val separatorString = if (prettyPrint) " " else ""
                    val fieldString = "\"" +fieldName + "\":" + separatorString + serializer.stringValue
                    _appendStringArrayItemToStringBuilder(fieldString, builder, isFirst)
                    
                }else{
                    _appendKeyValueToStringBuilder(fieldName, fieldValue, builder, isFirst)
                }
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

        var serializer: JSONSerializer = null
        if (skipObjectIDs){
            // If skipping object IDs, need to clone the array, otherwise we could detect
            // a cycle that's not really there
            serializer = new JSONSerializer(value, options, processedObjects.clone)
            serializer.context = context
        }else{
            // Do not clone the processedObjects as it will save space
            serializer = new JSONSerializer(value, options, processedObjects)
            serializer.context = context
        }
        var serializedObj: String = serializer.stringValue
        if (prettyPrint)
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        _appendStringArrayItemToStringBuilder("\"" + key + "\"" + separator + serializedObj, builder, isFirst)
    }

    /** Returns whether @f has a JSONConcreteArrayClass annotation.
      *
      * @param f The field.
      *
      * @return True or false.
      */
    private def _isConcreteArrayClassField(f: Field): Boolean = {
        f.getAnnotation(classOf[JSONConcreteArrayClass]) != null
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
        if (cl.getAnnotation(classOf[JSONUnnamedClass]) != null && !disableCustomSerialization){
            null
        }else if (cl.getAnnotation(classOf[JSONPoseableClass]) != null){
            val nameAnot: JSONPoseableClass = cl.getAnnotation(classOf[JSONPoseableClass])
            nameAnot.otherClass.getName
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
        customObj.fieldNamesForJSONSerialization(context) foreach { key => {
            _appendKeyValueToStringBuilder(key, customObj.fieldValueForKey(context, key), builder, index == 0)
            index += 1
        }}

        if (prettyPrint)
            builder.append('\n')

        builder.append('}')
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
        val map: scala.collection.Iterable[(String, _)] = obj.asInstanceOf[scala.collection.Iterable[(String, _)]]

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
        
        var matched: Boolean = false
        var serializedString: String = ""
        if (!disableCustomSerialization){
            // If the custom serialization isn't disabled, we need to try to match the traits
            if (obj.isInstanceOf[JSONSerializationFullyCustomized]) {
                matched = true
                serializedString = obj.asInstanceOf[JSONSerializationFullyCustomized].JSONValue(context, options)
            }else if (obj.isInstanceOf[JSONSerializationCustomFields]){
                matched = true
                serializedString = _serializeCustomObject
            }
        }

        if (!matched) {
            obj match {
                case _: String => JSONUtilities.escapedString(obj.asInstanceOf[String])
                case _: java.lang.Number => obj.toString
                case _: java.lang.Boolean => if (obj.asInstanceOf[java.lang.Boolean].booleanValue) "true"
                else "false"
                case _: java.lang.Character => JSONUtilities.escapedChar(obj.asInstanceOf[java.lang.Character].charValue())
                case _: Option[_] => _serializeOption
                case _: scala.collection.Map[_, _] => _serializeMap
                case _: scala.collection.Traversable[_] => _serializeTraversable
                case _: Array[_] => _serializeArray
                case _: AnyRef => _serializePlainObject
                case _ => _serializePrimitiveType
            }
        }else{
            serializedString
        }
    }

    /** obj has already been encountered - serialize it just as a reference - use objectID.
      * 
      * @return obj serialized as an object reference.
      */
    private def _serializeObjectAsReference: String = {
        val builder: StringBuilder = new StringBuilder("")
        _appendKeyValueToStringBuilder("__ref__", objectID, builder, true)
        builder.toString
    }

    /** The opposite of _serializeObjectAsReference.
      * 
      * @return obj serialized as value.
      */
    private def _serializeObjectAsValue: String = {
        val builder: StringBuilder = new StringBuilder("")

        // Get object's fields:
        val c: Class[_] = obj.getClass
        val fields: Array[Field] = c.getDeclaredFields

        var haveProcessedField: Boolean = false

        val className: String = _objectsClassName
        if (className != null) {
            _appendKeyValueToStringBuilder("__class__", className, builder, !haveProcessedField)
            haveProcessedField = true
        }

        if (!skipObjectIDs){
            _appendKeyValueToStringBuilder("__objectID__", objectID, builder, !haveProcessedField)
            haveProcessedField = true
        }

        for (i: Int <- 0 until fields.length) {
            if (_appendFieldToStringBuilder(fields(i), builder, !haveProcessedField)){
                haveProcessedField = true
            }
        }

        if (obj.isInstanceOf[JSONSerializationAdditionalFields] && !disableCustomSerialization){
            // Additional fields for the object
            val map: Map[String, Any] = obj.asInstanceOf[JSONSerializationAdditionalFields].additionalFieldsForJSONSerialization(context)
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

        builder.toString
    }
    
    
    private def _serializeOption: String = {
        val opt: Option[_] = obj.asInstanceOf[Option[_]]
        if (opt.isEmpty){
            "null"
        }else{
            val serializer = new JSONSerializer(opt.get, options, processedObjects)
            serializer.context = context
            serializer.stringValue
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
        
        if (serializeObjectAsReference){
           builder.append(_serializeObjectAsReference)
        }else{
            builder.append(_serializeObjectAsValue)
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

    /** Serializes an "array" - i.e. an object that implements
      *  the Traversable trait, yet isn't a map.
      *
      * @return JSON representation of obj.
      */
    private def _serializeTraversable: String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (prettyPrint)
            builder.append('\n')

        // We know it is Traversable
        val coll: scala.collection.Traversable[_] = obj.asInstanceOf[scala.collection.Traversable[_]]

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
