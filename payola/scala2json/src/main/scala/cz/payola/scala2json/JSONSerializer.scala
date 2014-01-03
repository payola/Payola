package cz.payola.scala2json

import classes.SerializationClass
import java.lang.reflect.{Field, Method}
import rules.{CustomValueSerializationRule, CustomSerializationRule, BasicSerializationRule, SerializationRule}
import scala.collection.mutable._
import cz.payola.scala2json.rules.BasicSerializationRule
import scala.StringBuilder
import cz.payola.scala2json.rules.CustomValueSerializationRule
import cz.payola.scala2json.rules.CustomSerializationRule
import scala.collection.mutable

/** This enumeration defines output format for the serializer.
  *
  * PrettyPrinted option creates a white-space-formatted output with new lines
  *     and tab-indented lines.
  * Condensed option, on the other hand, creates a compressed output,
  *     optimized for size - hence reduces network traffic when transmitting
  *     serialized objects.
  */
object OutputFormat extends Enumeration {

    type OutputFormat = Value

    val PrettyPrinted = Value

    val Condensed = Value
}

/** Scala object -> JSON serializer.
  *
  * Serializes any object to JSON. Uses several techniques for obtaining class fields:
  * - for regular objects without any rules, JSONSerializer uses Java reflection to obtain
  *     fields and lists them.
  * - this behavior can be modified using rules for particular classes:
  *     - CustomSerializationRule allows you to control the object serialization completely.
  *     - CustomValueSerializationRule lets you add additional field to the class.
  *     - BasicSerializationRule can be used to serialize just fields that are common with
  *         another class or a trait, skip some fields or rename the fields.
  *
  * In addition to this, each object is by default enriched by a "__class__" field, which
  * contains the name of the object's class, or the name of a class that the object is being
  * serialized as if defined so in a BasicSerializationRule.
  *
  * Each object is assigned an "__objectID__" field as well, which contains a number
  * that is unique within that particular serialization. If the serializer runs into the same
  * object for the second time, it is not serialized again, but a JSON object like the following
  * is entered:
  *
  * {
  *     "__ref__": 4
  * }
  *
  * i.e. it's a reference to an already-encountered object with __objectID__ 4.
  *
  * This behavior can be modified by setting the serializeInDepth option to true.
  * Then, objects are not assigned an __objectID__ and are serialized again at each
  * encounter. JSONSerializer will also detect any object cycles - upon discovering
  * such a cycle, an exception is raised.
  *
  * Collections: JSONSerializer can serialize any of the Scala collections. To be able to
  * deserialize the collection on the other end, however, a simple JSON array cannot be
  * used (it's used only for the generic Array[_] type). Instead, the collection is serialized
  * as a JSON object with two fields: __arrayClass__ and __value__. __arrayClass__
  * determines of which class the collection is and the __value__ field contains the actual
  * contents of the collection, now as a plain JSON array. Collections that inherit
  * from the scala.collection.Map are treated differently - they are simply serialized as
  * a plain JSON object.
  *
  * Thread-safety: Serialization of objects is generally thread-safe, however,
  * you should not be modifying the rules and settings (i.e. "includeClassFields",
  * "outputFormat", "serializeInDepth" options) while the serializer is serializing
  * an object.
  *
  * To use the serializer, simply create a new instance, set up all the rules and parameters
  * of the serialization and call the serialize method. An example follows:
  *
  * val serializer = new JSONSerializer()
  * println(serializer.serialize(myObject)
  */
class JSONSerializer
{
    var outputFormat = OutputFormat.Condensed

    var includeClassFields = true

    var serializeInDepth = false

    private val _rules = new ArrayBuffer[(SerializationClass, SerializationRule)]()

    /** Adds fields from @fieldToAdd to @fields in a name-distinct manner (i.e. if there
      * already exists a field with the same name, it will not be added again).
      *
      * @param fieldsToAdd Fields to be added.
      * @param fields Field list buffer where to add the fields.
      */
    private def addNameDistinctFieldsToListBuffer(fieldsToAdd: collection.Seq[Field], fields: ListBuffer[Field]) {
        fieldsToAdd foreach { f: Field =>
            if (!fields.exists(_.getName == f.getName)){
                fields += f
            }
        }
    }

    /** Add a serialization rule.
      *
      * @param forClass For which class.
      * @param rule Which rule.
      */
    def addSerializationRule(forClass: SerializationClass, rule: SerializationRule) {
        _rules += ((forClass, rule))
    }

    private def classHasField(cl: Class[_], fName: String): Boolean = {
        if (cl.isInterface) {
            // I.e. a trait, treat it differently
            // Maybe, there is a field, so let's try finding it just to be sure
            var found = getFieldsForClass(cl).exists {f: Field => f.getName == fName}
            if (!found) {
                // Otherwise, we need a little magic - find a method whose name is the same
                // and take one parameter of type cl
                found = cl.getMethods.exists {m: Method =>
                    val paramTypes: Array[Class[_]] = m.getParameterTypes
                    (m.getName == fName && (paramTypes.length == 0 || (paramTypes.length == 1 && paramTypes(0) == cl)))
                }
            }

            if (!found) {
                // There's one more place the field can be hiding - interfaces that this interface extends
                found = cl.getInterfaces.exists {intCl: Class[_] => classHasField(intCl, fName)}
            }
            found
        } else {
            // Actual object class
            getFieldsForClass(cl).exists {f: Field => f.getName == fName}
        }
    }

    /** Retrieves fields from all superclasses and interfaces.
      *
      * @param cl Class.
      * @return All fields.
      */
    private def getFieldsForClass(cl: Class[_]): Seq[Field] = {
        val fields = new ListBuffer[Field]()
        listFieldsForClass(cl, fields)
        fields
    }

    /** Retrieves fields from all superclasses and interfaces and stores them in the fields
      * list buffer.
      *
      * @param cl Class.
      * @param fields Fields list buffer.
      * @return All fields.
      */
    private def listFieldsForClass(cl: Class[_], fields: ListBuffer[Field]) {
        addNameDistinctFieldsToListBuffer(cl.getDeclaredFields, fields)

        if (cl.getSuperclass != null) {
            listFieldsForClass(cl.getSuperclass, fields)
        }

        cl.getInterfaces foreach { interface: Class[_] =>
            listFieldsForClass(interface, fields)
        }
    }

    /** Returns the object's class name.
      *
      * @return The object's class name.
      */
    private def objectsClassName(obj: AnyRef): String = {
        obj match {
            case _: scala.collection.immutable.List[_] => "scala.collection.immutable.List"
            case _ => obj.getClass.getCanonicalName
        }
    }

    private def prettyPrint: Boolean = outputFormat == OutputFormat.PrettyPrinted

    /** Serializes @obj to a JSON string.
      *
      * @return JSON representation of obj.
      */
    def serialize(obj: Any): String = {
        // If obj is null, return "null" - as defined at http://www.json.org/
        serializeObject(obj, new ArrayBuffer[Any]())
    }

    /** Serializes an Array[_]
      *
      * @return JSON representation of obj.
      */
    private def serializeArray(arr: Array[_], processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "[")
        val builder = jsonBuilder.stringBuilder
        if (prettyPrint) {
            builder.append('\n')
        }

        for (i: Int <- 0 until arr.length) {
            jsonBuilder.appendArrayItem(arr(i), i == 0, processedObjects)
        }

        if (prettyPrint) {
            builder.append('\n')
        }

        builder.append(']')
        builder.toString
    }

    /** Serializes an object that implements
      *  the Map trait, yet isn't a map.
      *
      *  @param map The map.
      *  @param processedObjects Already processed objects.
      *
      *  @return JSON representation of obj.
      */
    private def serializeMap(map: scala.collection.Map[_, _], processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "{")
        val builder = jsonBuilder.stringBuilder
        if (prettyPrint) {
            builder.append('\n')
        }

        jsonBuilder.appendKeySerializedValue("__class__", map.getClass.getName, true)

        var serializedMapString = serializeMapAsPlainMap(map, processedObjects)
        if (outputFormat == OutputFormat.PrettyPrinted) {
            serializedMapString = JSONUtilities.padStringWithTab(serializedMapString)
        }

        jsonBuilder.appendKeySerializedValue("__value__", serializedMapString, false)

        if (prettyPrint) {
            builder.append('\n')
        }

        builder.append('}')
        builder.toString
    }

    /** Serializes the map as a plain object, i.e. key-value pairs.
      *
      * @param map The map.
      * @param processedObjects Already processed objects.
      * @return JSON representation of the map.
      */
    private def serializeMapAsPlainMap(map: scala.collection.Map[_, _], processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "{")
        val builder = jsonBuilder.stringBuilder
        if (prettyPrint) {
            builder.append('\n')
        }

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        map foreach {
            case (key:String, value) => {
                jsonBuilder.appendKeyValue(key, value, index == 0, processedObjects)
                index += 1
            }
            case _ => throw new JSONSerializationException("Maps with other than String keys are not allowed.")
        }

        if (prettyPrint) {
            builder.append('\n')
        }

        builder.append('}')
        builder.toString
    }

    /** Matches the @obj's type and calls the appropriate method.
      *
      * @return JSON representation of obj.
      */
    private[scala2json] def serializeObject(obj: Any, processedObjects: ArrayBuffer[Any]): String = {
        var objectID: Int = 0
        var serializeObjectAsReference: Boolean = false

        if (processedObjects.contains(obj)) {
            if (serializeInDepth) {
                // Skipping IDs -> can't serialize a cycle
                throw new JSONSerializationException("Cycle detected on object " + obj + ".")
            } else {
                // Otherwise, fetch the objectID and serialize it as a reference
                objectID = processedObjects.indexOf(obj)
                serializeObjectAsReference = true
            }
        } else if (obj.isInstanceOf[AnyRef]
            && !obj.isInstanceOf[String]
            && !obj.isInstanceOf[java.lang.Number]
            && !obj.isInstanceOf[java.lang.Boolean]
            && !obj.isInstanceOf[java.lang.Character]) {
            // Do not detect cycles on primitive types
            objectID = processedObjects.size // The index will be at the size of the array
            processedObjects += obj
        }

        val serializationClass = _rules find {
            case (cl: SerializationClass, rule: SerializationRule) =>
                cl.isClassOf(obj)
        }

        var result = ""
        // Skip custom serialization if serializing as reference
        if (serializeObjectAsReference
            && obj != None
            && !obj.isInstanceOf[collection.Map[_, _]]
            && !obj.isInstanceOf[collection.Traversable[_]]
            && !obj.isInstanceOf[Array[_]]) {
            // Must be AnyRef, otherwise it wouldn't get into the processedObjects array
            result = serializePlainObject(obj.asInstanceOf[AnyRef], serializeObjectAsReference, objectID, processedObjects)
        } else if (serializationClass.isDefined && !serializationClass.get._2.isInstanceOf[CustomValueSerializationRule[_]]) {
            val rule = serializationClass.get._2
            result = rule match {
                case basic: BasicSerializationRule => serializeWithRule(obj.asInstanceOf[AnyRef], basic, objectID, processedObjects)
                case custom: CustomSerializationRule => custom.customSerializer(this, obj, objectID)
                case _ => {
                    println("Unknown rule type - " + rule)
                    ""
                }
            }
        } else {
            result = obj match {
                case s: String => {
                    if (s.startsWith("#!json~*")){
                        s.replace("#!json~*","")
                    }else{
                        JSONUtilities.escapeString(obj.asInstanceOf[String])
                    }
                }
                case _: java.lang.Number => obj.asInstanceOf[java.lang.Number].toString
                case _: java.lang.Boolean => if (obj.asInstanceOf[java.lang.Boolean].booleanValue) "true" else "false"
                case _: java.lang.Character => JSONUtilities
                    .escapeChar(obj.asInstanceOf[java.lang.Character].charValue())
                case None => "scala.None.get()"
                case map: scala.collection.Map[_, _] => serializeMap(map, processedObjects)
                case trav: scala.collection.Traversable[_] => serializeTraversable(trav, processedObjects)
                case arr: Array[_] => serializeArray(arr, processedObjects)
                case ref: AnyRef => serializePlainObject(ref, serializeObjectAsReference, objectID, processedObjects)
                case rest => serializePrimitiveType(rest)
            }
        }

        if (serializeInDepth) {
            // We're going back in the tree
            processedObjects -= obj
        }

        result
    }

    /** obj has already been encountered - serialize it just as a reference - use objectID.
      *
      * @return obj serialized as an object reference.
      */
    private def serializeObjectAsReference(obj: AnyRef, objectID: Int, processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "")
        val builder = jsonBuilder.stringBuilder
        jsonBuilder.appendKeyValue("__ref__", objectID, true, processedObjects)
        builder.toString
    }

    /** The opposite of serializeObjectAsReference.
      *
      * @return obj serialized as value.
      */
    private def serializeObjectAsValue(obj: AnyRef, objectID: Int,
        transientFields: Option[collection.Seq[String]] = None,
        fieldAliases: Option[collection.Map[String, String]] = None,
        processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "")
        val builder = jsonBuilder.stringBuilder

        // Get object's fields:
        val c: Class[_] = obj.getClass
        val fields = getFieldsForClass(c)

        var haveProcessedField: Boolean = false

        if (includeClassFields) {
            // => include __class__ field
            val className: String = objectsClassName(obj)
            jsonBuilder.appendKeyValue("__class__", className, !haveProcessedField, processedObjects)
            haveProcessedField = true
        }

        if (!serializeInDepth) {
            // => include __objectID__field
            jsonBuilder.appendKeyValue("__objectID__", objectID, !haveProcessedField, processedObjects)
            haveProcessedField = true
        }

        // Process all fields
        for (i: Int <- 0 until fields.length) {
            val f: Field = fields(i)
            f.setAccessible(true)
            var fName: String = f.getName
            if (transientFields.isEmpty || !transientFields.get.contains(fName)) {
                if (fieldAliases.isDefined && fieldAliases.get.contains(fName)) {
                    fName = fieldAliases.get.get(fName).get
                }

                val fValue = f.get(obj)
                if (fValue == null) {
                    jsonBuilder.appendKeySerializedValue(fName, "null", !haveProcessedField)
                } else {
                    jsonBuilder.appendKeyValue(fName, fValue, !haveProcessedField, processedObjects)
                }
                haveProcessedField = true
            }
        }
        
        _rules foreach { item: (SerializationClass, SerializationRule) =>
            if (item._1.isClassOf(obj) && item._2.isInstanceOf[CustomValueSerializationRule[_]]){
                val rule: CustomValueSerializationRule[AnyRef] = item._2.asInstanceOf[CustomValueSerializationRule[AnyRef]]
                jsonBuilder.appendKeyValue(rule.fieldName, rule.definingFunction(this, obj), !haveProcessedField, processedObjects)
                haveProcessedField = true
            }
        }

        builder.toString
    }

    private def serializeObjectPosingAsClass(obj: AnyRef, objectID: Int, targetClass: Class[_],
        transientFields: Option[collection.Seq[String]],
        fieldAliases: Option[collection.Map[String, String]],
        processedObjects: ArrayBuffer[Any]): String = {

        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "")
        val builder = jsonBuilder.stringBuilder

        // Get object's fields:
        val originalClass: Class[_] = obj.getClass
        val originalFields = getFieldsForClass(originalClass)

        var haveProcessedField: Boolean = false

        if (includeClassFields) {
            // => include __class__ field
            val className: String = targetClass.getCanonicalName
            jsonBuilder.appendKeyValue("__class__", className, !haveProcessedField, processedObjects)
            haveProcessedField = true
        }

        if (!serializeInDepth) {
            // => include __objectID__field
            jsonBuilder.appendKeyValue("__objectID__", objectID, !haveProcessedField, processedObjects)
            haveProcessedField = true
        }

        // Process all fields
        for (i: Int <- 0 until originalFields.length) {
            val originalField: Field = originalFields(i)
            originalField.setAccessible(true)
            var originalFieldName: String = originalField.getName

            if (transientFields.isEmpty || !transientFields.get.contains(originalFieldName)) {
                if (fieldAliases.isDefined && fieldAliases.get.contains(originalFieldName)) {
                    originalFieldName = fieldAliases.get.get(originalFieldName).get
                }

                // Check is the class has this field as well
                if (classHasField(targetClass, originalFieldName)) {
                    val fieldValue = originalField.get(obj)
                    if (fieldValue == null) {
                        jsonBuilder.appendKeySerializedValue(originalFieldName, "null", !haveProcessedField)
                    } else {
                        jsonBuilder.appendKeyValue(originalFieldName, fieldValue, !haveProcessedField, processedObjects)
                    }
                    haveProcessedField = true
                }
            }
        }

        _rules foreach { item: (SerializationClass, SerializationRule) =>
            if (item._1.isClassOf(obj) && item._2.isInstanceOf[CustomValueSerializationRule[_]]){
                val rule: CustomValueSerializationRule[AnyRef] = item._2.asInstanceOf[CustomValueSerializationRule[AnyRef]]
                jsonBuilder.appendKeyValue(rule.fieldName, rule.definingFunction(this, obj), !haveProcessedField, processedObjects)
                haveProcessedField = true
            }
        }

        builder.toString
    }

    /** Serializes an object - generally AnyRef
      *
      * For most types, just calls obj.toString, the exception is
      * Boolean, which is converted to 'true' or 'false', Char is converted
      * to String. When Unit is encountered, an exception is raised.
      *
      * @return JSON value.
      */
    private def serializePlainObject(obj: AnyRef, asReference: Boolean, objectID: Int, processedObjects: ArrayBuffer[Any]): String = {
        // Now we're dealing with some kind of an object,
        // we'll use Java's reflection to serialize it
        val builder: StringBuilder = new StringBuilder("{")

        if (prettyPrint) {
            builder.append('\n')
        }

        if (asReference) {
            builder.append(serializeObjectAsReference(obj, objectID, processedObjects))
        } else {
            builder.append(serializeObjectAsValue(obj, objectID, processedObjects = processedObjects))
        }

        if (prettyPrint) {
            builder.append('\n')
        }

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
    private def serializePrimitiveType(obj: Any): String = {
        obj match {
            case _: Boolean => if (obj.asInstanceOf[Boolean]) "true" else "false"
            case _: Char => JSONUtilities.escapeChar(obj.asInstanceOf[Char])
            case _: Unit => throw new JSONSerializationException("Cannot serialize Unit.")
            case _ => if (obj != null) {obj.toString}else{ "null" }
        }
    }

    /** Serializes an "array" - i.e. an object that implements
      *  the Traversable trait, yet isn't a map.
      *
      * @return JSON representation of obj.
      */

    private def serializeTraversable(coll: scala.collection.Traversable[_], processedObjects: ArrayBuffer[Any]): String = {
        if (includeClassFields) {
            val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "{")
            val builder = jsonBuilder.stringBuilder
            if (prettyPrint) {
                builder.append('\n')
            }

            jsonBuilder.appendKeyValue("__arrayClass__", objectsClassName(coll), true, processedObjects)

            var serializedValue = serializeTraversableValue(coll, processedObjects)
            if (prettyPrint) {
                serializedValue = JSONUtilities.padStringWithTab(serializedValue)
            }
            jsonBuilder.appendKeySerializedValue("__value__", serializedValue, false)

            if (prettyPrint) {
                builder.append('\n')
            }

            builder.append('}')
            builder.toString
        } else {
            serializeTraversableValue(coll, processedObjects)
        }
    }

    private def serializeTraversableValue(coll: scala.collection.Traversable[_], processedObjects: ArrayBuffer[Any]): String = {
        val jsonBuilder: JSONStringBuilder = new JSONStringBuilder(this, prettyPrint, "[")
        val builder = jsonBuilder.stringBuilder
        if (prettyPrint) {
            builder.append('\n')
        }

        // Need to keep track of index so that
        // we don't add a comma after the first iteration
        var index: Int = 0
        coll foreach {item => {
            jsonBuilder.appendArrayItem(item, index == 0, processedObjects)
            index += 1
        }
        }

        if (prettyPrint) {
            builder.append('\n')
        }

        builder.append(']')
        builder.toString
    }

    private def serializeWithRule(obj: AnyRef, rule: BasicSerializationRule, objectID: Int, processedObjects: ArrayBuffer[Any]): String = {
        val builder: StringBuilder = new StringBuilder("{")
        if (prettyPrint) {
            builder.append('\n')
        }

        if (rule.serializeAsClass.isEmpty) {
            builder.append(serializeObjectAsValue(obj, objectID, rule.transientFields, rule.fieldAliases, processedObjects))
        } else {
            // Custom
            builder.append(serializeObjectPosingAsClass(obj, objectID, rule.serializeAsClass.get, rule.transientFields,
                rule.fieldAliases, processedObjects))
        }

        if (prettyPrint) {
            builder.append('\n')
        }

        builder.append('}')
        builder.toString
    }

}
