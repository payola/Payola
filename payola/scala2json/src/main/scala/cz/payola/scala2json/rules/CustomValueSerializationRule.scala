package cz.payola.scala2json.rules

import cz.payola.scala2json.JSONSerializer

/** Adds a way to add a field to a class which can be dynamically computed using
  * the definingFunction.
  *
  * @param fieldName Name of the new field.
  * @param definingFunction A function that returns a value of the field. The function
  *                     takes two parameters - first is the JSONSerializer, second is the object
  *                     that is being serialized.
  */
case class CustomValueSerializationRule[T](fieldName: String,
                                                              definingFunction: (JSONSerializer, T) => Any) extends SerializationRule
{
}
