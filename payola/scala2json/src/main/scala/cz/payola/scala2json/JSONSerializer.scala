package cz.payola.scala2json


import cz.payola.scala2json.annotations._
import java.lang.reflect.Field
import scala.collection.mutable._

object OutputFormat extends Enumeration
{
    type OutputFormat = Value

    val PrettyPrinted = Value
    val Condensed = Value
}

class JSONSerializer {

    var outputFormat = OutputFormat.Condensed
    var includeClassFields = true
    var serializeInDepth = false

    private def prettyPrint: Boolean = outputFormat == OutputFormat.PrettyPrinted

    private val processedObjects = new ArrayBuffer[Any]()
    
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
        var serializedObj: String = _serializeObject(item)
        if (prettyPrint){
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
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
    private def _appendFieldToStringBuilder(obj: AnyRef, f: Field, builder: StringBuilder, isFirst: Boolean): Boolean = {
        f.setAccessible(true)

        if (_isFieldTransient(f)){
            false
        }else{
            val fieldName = _nameOfField(f)
            val fieldValue = f.get(obj)
            if (fieldValue == null){
                _appendKeySerializedValueToStringBuilder(fieldName, "null", builder, isFirst)
                true
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
        var serializedObj: String = _serializeObject(value)
        if (prettyPrint){
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        _appendKeySerializedValueToStringBuilder(key, serializedObj, builder, isFirst)
    }

    private def _appendKeySerializedValueToStringBuilder(key: String, value: String, builder: StringBuilder, isFirst: Boolean) = {
        val separator: String = if (prettyPrint) ": " else ":"
        val field: String = "\"" + key + "\"" + separator + value
        _appendStringArrayItemToStringBuilder(field, builder, isFirst)
    }

    /** Returns whether @f has a JSONTransient annotation.
     *
     * @param f The field.
     *
     * @return True or false.
     */
    private def _isFieldTransient(f: Field): Boolean = {
        false
    }

    /** Returns the field's name - if it has a JSONFieldName annotation,
     *  it uses that.
     *
     *  @param f The field.
     *
     *  @return The field's name, considering annotations.
     */
    private def _nameOfField(f: Field): String = {
        f.getName
    }

    /** Returns the object's class name.
      *
      * @return The object's class name.
      */
    private def _objectsClassName(obj: AnyRef): String = {
        obj.getClass.getCanonicalName
    }

    /** Serializes an Array[_]
     *
     * @return JSON representation of obj.
     */
    private def _serializeArray(arr: Array[_]): String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (prettyPrint)
            builder.append('\n')

        for (i: Int <- 0 until arr.length) {
            _appendArrayItemToStringBuilder(arr(i), builder, i == 0)
        }

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
    private def _serializeMap(map: scala.collection.Map[_, _]): String = {
        val builder: StringBuilder = new StringBuilder("{")
        if (prettyPrint)
            builder.append('\n')
        
        // We know it is a Map[String, _]
        val mapIt: scala.collection.Iterable[(String, _)] = map.asInstanceOf[scala.collection.Iterable[(String, _)]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        mapIt foreach {case (key, value) => {
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
    private def _serializeObject(obj: Any): String = {

        var objectID: Int = 0
        var serializeObjectAsReference: Boolean = false

        if (processedObjects.contains(obj)){
            if (serializeInDepth){
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

        val result: String = obj match {
            case _: String => JSONUtilities.escapedString(obj.asInstanceOf[String])
            case _: java.lang.Number => obj.asInstanceOf[java.lang.Number].toString
            case _: java.lang.Boolean => if (obj.asInstanceOf[java.lang.Boolean].booleanValue) "true" else "false"
            case _: java.lang.Character => JSONUtilities.escapedChar(obj.asInstanceOf[java.lang.Character].charValue())
            case opt: Option[_] => _serializeOption(opt)
            case map: scala.collection.Map[_, _] => _serializeMap(map)
            case trav: scala.collection.Traversable[_] => _serializeTraversable(trav)
            case arr: Array[_] => _serializeArray(arr)
            case ref: AnyRef => _serializePlainObject(ref, serializeObjectAsReference, objectID)
            case rest => _serializePrimitiveType(rest)
        }

        if (serializeInDepth){
            // We're going back in the tree
            processedObjects -= obj
        }

        result
    }

    /** obj has already been encountered - serialize it just as a reference - use objectID.
      * 
      * @return obj serialized as an object reference.
      */
    private def _serializeObjectAsReference(obj: AnyRef, objectID: Int): String = {
        val builder: StringBuilder = new StringBuilder("")
        _appendKeyValueToStringBuilder("__ref__", objectID, builder, true)
        builder.toString
    }

    /** The opposite of _serializeObjectAsReference.
      * 
      * @return obj serialized as value.
      */
    private def _serializeObjectAsValue(obj: AnyRef, objectID: Int): String = {
        val builder: StringBuilder = new StringBuilder("")

        // Get object's fields:
        val c: Class[_] = obj.getClass
        val fields: Array[Field] = c.getDeclaredFields

        var haveProcessedField: Boolean = false

        if (includeClassFields) {
            // => include __class__ field
            val className: String = _objectsClassName(obj)
            _appendKeyValueToStringBuilder("__class__", className, builder, !haveProcessedField)
            haveProcessedField = true
        }

        if (!serializeInDepth) {
            // => include __objectID__field
            _appendKeyValueToStringBuilder("__objectID__", objectID, builder, !haveProcessedField)
            haveProcessedField = true
        }

        // Process all fields
        for (i: Int <- 0 until fields.length) {
            if (_appendFieldToStringBuilder(obj, fields(i), builder, !haveProcessedField)){
                haveProcessedField = true
            }
        }

        builder.toString
    }

    /**
      *
      * @param opt
      * @return
      */
    private def _serializeOption(opt: Option[_]): String = {
        if (opt.isEmpty){
            "null"
        }else{
            _serializeObject(opt.get)
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
    private def _serializePlainObject(obj: AnyRef, serializeObjectAsReference: Boolean, objectID: Int): String = {
        // Now we're dealing with some kind of an object,
        // we'll use Java's reflection to serialize it
        val builder: StringBuilder = new StringBuilder("{")

        if (prettyPrint)
            builder.append('\n')
        
        if (serializeObjectAsReference){
           builder.append(_serializeObjectAsReference(obj, objectID))
        }else{
            builder.append(_serializeObjectAsValue(obj, objectID))
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
    private def _serializePrimitiveType(obj: Any): String = {
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
    private def _serializeTraversable(coll: scala.collection.Traversable[_]): String = {
        if (includeClassFields){
            val builder: StringBuilder = new StringBuilder("{")
            if (prettyPrint){
                builder.append('\n')
            }

            _appendKeyValueToStringBuilder("__class__", _objectsClassName(coll), builder, true)
            _appendKeySerializedValueToStringBuilder("__value__", _serializeTraversableValue(coll), builder, false)

            if (prettyPrint) {
                builder.append('\n')
            }

            builder.append('}')
            builder.toString
        }else{
            _serializeTraversableValue(coll)
        }
    }
    
    private def _serializeTraversableValue(coll: scala.collection.Traversable[_]): String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (prettyPrint)
            builder.append('\n')

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
   def serialize(obj: Any): String = {
       // If obj is null, return "null" - as defined at http://www.json.org/
       if (obj == null){
           "null"
       }else{
           val result = _serializeObject(obj)
           // Need to clear processed objects for next use
           processedObjects.clear
           result
       }
   }

}
