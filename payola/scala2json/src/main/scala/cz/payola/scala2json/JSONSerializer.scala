package cz.payola.scala2json

import classes.SerializationClass
import cz.payola.scala2json.annotations._
import java.lang.reflect.Field
import rules.{CustomSerializationRule, BasicSerializationRule, SerializationRule}
import scala.collection.mutable.{ArrayBuffer, HashMap}

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

    private val _processedObjects = new ArrayBuffer[Any]()
    private val _rules = new HashMap[SerializationClass, SerializationRule]()
    
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
            if (_prettyPrint){
                builder.append('\n')
            }
        }

        if (_prettyPrint){
            builder.append('\t')
        }

        builder.append(item)
    }

    private def _appendArrayItemToStringBuilder(item: Any, builder: StringBuilder, isFirst: Boolean) = {
        var serializedObj: String = _serializeObject(item)
        if (_prettyPrint){
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        _appendStringArrayItemToStringBuilder(serializedObj, builder, isFirst)
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
        if (_prettyPrint){
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        _appendKeySerializedValueToStringBuilder(key, serializedObj, builder, isFirst)
    }

    private def _appendKeySerializedValueToStringBuilder(key: String, value: String, builder: StringBuilder, isFirst: Boolean) = {
        val separator: String = if (_prettyPrint) ": " else ":"
        val field: String = "\"" + key + "\"" + separator + value
        _appendStringArrayItemToStringBuilder(field, builder, isFirst)
    }

    /** Returns the object's class name.
      *
      * @return The object's class name.
      */
    private def _objectsClassName(obj: AnyRef): String = {
        obj.getClass.getCanonicalName
    }

    private def _prettyPrint: Boolean = outputFormat == OutputFormat.PrettyPrinted

    /** Serializes an Array[_]
     *
     * @return JSON representation of obj.
     */
    private def _serializeArray(arr: Array[_]): String = {
        val builder: StringBuilder = new StringBuilder("[")
        if (_prettyPrint)
            builder.append('\n')

        for (i: Int <- 0 until arr.length) {
            _appendArrayItemToStringBuilder(arr(i), builder, i == 0)
        }

        if (_prettyPrint)
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
        if (_prettyPrint){
            builder.append('\n')
        }
        
        // We know it is a Map[String, _]
        val mapIt: scala.collection.Iterable[(String, _)] = map.asInstanceOf[scala.collection.Iterable[(String, _)]]

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        mapIt foreach {case (key, value) => {
            _appendKeyValueToStringBuilder(key, value, builder, index == 0)
            index += 1
        }}

        if (_prettyPrint){
            builder.append('\n')
        }
        
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

        if (_processedObjects.contains(obj)){
            if (serializeInDepth){
                // Skipping IDs -> can't serialize a cycle
                throw new JSONSerializationException("Cycle detected on object " + obj + ".")
            }else{
                // Otherwise, fetch the objectID and serialize it as a reference
                objectID = _processedObjects.indexOf(obj)
                serializeObjectAsReference = true
            }
        }else if (obj.isInstanceOf[AnyRef]
            && !obj.isInstanceOf[String]
            && !obj.isInstanceOf[java.lang.Number]
            && !obj.isInstanceOf[java.lang.Boolean]
            && !obj.isInstanceOf[java.lang.Character]){
            // Do not detect cycles on primitive types
            objectID = _processedObjects.size // The index will be at the size of the array
            _processedObjects += obj
        }

        val serializationClass = _rules find { case (cl: SerializationClass, rule: SerializationRule) =>
            cl.isClassOf(obj)
        }

        var result = ""
        // Skip custom serialization if serializing as reference
        if (serializeObjectAsReference){
            // Must be AnyRef, otherwise it wouldn't get into the processedObjects array
            _serializePlainObject(obj.asInstanceOf[AnyRef], serializeObjectAsReference, objectID)
        }else if (serializationClass.isDefined){
            val rule = serializationClass.get._2
            result = rule match {
                case basic: BasicSerializationRule => _serializeWithRule(obj.asInstanceOf[AnyRef], basic, objectID)
                case custom: CustomSerializationRule => custom.customSerializer(this, obj, objectID)
                case _ => {
                    println("Unknown rule type - " + rule)
                    ""
                }
            }
        }else{
            result = obj match {
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
        }

        if (serializeInDepth){
            // We're going back in the tree
            _processedObjects -= obj
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
    private def _serializeObjectAsValue(obj: AnyRef, objectID: Int, transientFields: Option[collection.Seq[String]] = None, fieldAliases: Option[collection.Map[String, String]] = None): String = {
        println("As value")

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
            val f: Field = fields(i)
            f.setAccessible(true)
            var fName: String = f.getName
            if (transientFields.isEmpty || !transientFields.get.contains(fName)){
                if (fieldAliases.isDefined && fieldAliases.get.contains(fName)){
                    fName = fieldAliases.get.get(fName).get
                }

                val fValue = f.get(obj)
                if (fValue == null){
                    _appendKeySerializedValueToStringBuilder(fName, "null", builder, !haveProcessedField)
                }else{
                    _appendKeyValueToStringBuilder(fName, fValue, builder, !haveProcessedField)
                }
                haveProcessedField = true
            }
        }

        builder.toString
    }
    
    def _serializeObjectPosingAsClass(obj: AnyRef,  objectID: Int, targetClass: Class[_], transientFields: Option[collection.Seq[String]], fieldAliases: Option[collection.Map[String, String]]): String = {
        println("Posing as " + targetClass)
        val builder: StringBuilder = new StringBuilder("")

        // Get object's fields:
        val originalClass: Class[_] = obj.getClass
        val originalFields: Array[Field] = originalClass.getDeclaredFields
        val targetFields: Array[Field] = targetClass.getDeclaredFields

        var haveProcessedField: Boolean = false

        if (includeClassFields) {
            // => include __class__ field
            val className: String = targetClass.getCanonicalName
            _appendKeyValueToStringBuilder("__class__", className, builder, !haveProcessedField)
            haveProcessedField = true
        }

        if (!serializeInDepth) {
            // => include __objectID__field
            _appendKeyValueToStringBuilder("__objectID__", objectID, builder, !haveProcessedField)
            haveProcessedField = true
        }

        // Process all fields
        for (i: Int <- 0 until targetFields.length) {
            val targetField: Field = targetFields(i)
            targetField.setAccessible(true)
            var targetFieldName: String = targetField.getName
            val originalField: Option[Field] = originalFields find {f: Field => f.getName == targetFieldName}
            require(originalField.isDefined, "Cannot find field " + targetFieldName + " in object " + obj)
            originalField.get.setAccessible(true)

            if (transientFields.isEmpty || !transientFields.get.contains(targetFieldName)){
                if (fieldAliases.isDefined && fieldAliases.get.contains(targetFieldName)){
                    targetFieldName = fieldAliases.get.get(targetFieldName).get
                }

                val fieldValue = originalField.get.get(obj)
                if (fieldValue == null){
                    _appendKeySerializedValueToStringBuilder(targetFieldName, "null", builder, !haveProcessedField)
                }else{
                    _appendKeyValueToStringBuilder(targetFieldName, fieldValue, builder, !haveProcessedField)
                }
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
      * to String. When Unit is encountered, an exception is raised.
      *
      * @return JSON value.
      */
    private def _serializePlainObject(obj: AnyRef, serializeObjectAsReference: Boolean, objectID: Int): String = {
        // Now we're dealing with some kind of an object,
        // we'll use Java's reflection to serialize it
        val builder: StringBuilder = new StringBuilder("{")

        if (_prettyPrint)
            builder.append('\n')
        
        if (serializeObjectAsReference){
           builder.append(_serializeObjectAsReference(obj, objectID))
        }else{
            builder.append(_serializeObjectAsValue(obj, objectID))
        }

        if (_prettyPrint)
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
            if (_prettyPrint){
                builder.append('\n')
            }

            _appendKeyValueToStringBuilder("__arrayClass__", _objectsClassName(coll), builder, true)
            
            var serializedValue = _serializeTraversableValue(coll)
            if (_prettyPrint){
                serializedValue = serializedValue.replaceAllLiterally("\n", "\n\t")
            }
            _appendKeySerializedValueToStringBuilder("__value__", serializedValue, builder, false)

            if (_prettyPrint) {
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
        if (_prettyPrint)
            builder.append('\n')

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        coll foreach { item => {
            _appendArrayItemToStringBuilder(item, builder, index == 0)
            index += 1
        }}

        if (_prettyPrint){
            builder.append('\n')
        }

        builder.append(']')
        builder.toString
    }

    private def _serializeWithRule(obj: AnyRef, rule: BasicSerializationRule, objectID: Int): String = {
        val builder: StringBuilder = new StringBuilder("{")
        if (_prettyPrint){
            builder.append('\n')
        }
        
        if (rule.serializeAsClass.isEmpty){
            builder.append(_serializeObjectAsValue(obj, objectID, rule.transientFields, rule.fieldAliases))
        }else{
            // Custom
            builder.append(_serializeObjectPosingAsClass(obj, objectID, rule.serializeAsClass.get, rule.transientFields, rule.fieldAliases))
        }

        if (_prettyPrint){
            builder.append('\n')
        }

        builder.append('}')
        builder.toString
    }

    def addSerializationRule(forClass: SerializationClass, rule: SerializationRule) = _rules.put(forClass, rule)
    
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
           _processedObjects.clear
           result
       }
   }

}
