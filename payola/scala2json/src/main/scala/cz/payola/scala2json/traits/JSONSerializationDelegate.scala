package cz.payola.scala2json.traits

import cz.payola.scala2json.JSONSerializer
import collection.immutable.HashMap

trait JSONSerializationDelegate{
    def classNameForObject(s: JSONSerializer, obj: Any): Option[String] = Some(obj.getClass.getCanonicalName)
    
    def includeAdditionalFieldsForObject(s: JSONSerializer, obj: Any): Boolean = false
    def additionalFieldsForObject(s: JSONSerializer, obj: Any): Map[String, Any] = new HashMap[String, Any]()
    
    def fieldHasCustomValueInObject(s: JSONSerializer, fieldName: String,  obj: Any): Boolean = false
    def customValueForFieldInObject(s: JSONSerializer, fieldName: String,  obj: Any,  originalValue: Any) = originalValue
    
    def nameForFieldInObject(s: JSONSerializer, fieldName: String,  obj: Any): String = fieldName
    
    def shouldSerializeFieldInObject(s: JSONSerializer, fieldName: String, obj: Any): Boolean = true
    
    def shouldUseCustomSerializationForObject(s: JSONSerializer, obj: Any): Boolean = false
    def customJSONStringForObject(obj: Any): String = ""
    
}
