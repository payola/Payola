package cz.payola.scala2json.rules

import cz.payola.scala2json.JSONSerializer

/** This class allows you to serialize the object completely according to your needs.
  *
  * @param customSerializer This function that you need to pass has three arguments:
  * - the JSONSerializer from which you can read the serialization options (output format, etc.)
  * - the object itself to serialize
  * - object ID assigned to this particular object by the serializer. You should use it in the
  *     __objectID__ field of your class.
  *
  */
case class CustomSerializationRule(customSerializer: ((JSONSerializer, Any, Int) => String))
