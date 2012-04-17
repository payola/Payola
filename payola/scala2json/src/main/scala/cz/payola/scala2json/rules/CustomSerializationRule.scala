package cz.payola.scala2json.rules

import cz.payola.scala2json.JSONSerializer

// Serializer, Object to serialize, Object ID assigned by the serializer during serialization
case class CustomSerializationRule(customSerializer: ((JSONSerializer, Any, Int) => String))
