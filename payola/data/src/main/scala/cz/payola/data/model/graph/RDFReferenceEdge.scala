package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.JSONUnnamedClass

@JSONUnnamedClass
class RDFReferenceEdge(val graph: RDFGraph, val namespace: String, val name: String, val URI: String) extends RDFEdge {
    /** @see JSONSerializationCustomFields
      *
      * @return Iterable collection for the field names.
      */
    override def fieldNamesForJSONSerialization: Iterable[String] = {
        // Just add the "URI" field
        val sfields: Iterable[String] = super.fieldNamesForJSONSerialization
        var fields: List[String] = List("URI")
        sfields foreach {item => fields = item :: fields}
        fields
    }

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    override def fieldValueForKey(key: String): Any = {
        val fv = super.fieldValueForKey(key)
        if (fv != null) {
            fv
        }else{
            key match {
                case "URI" => URI
                case _ => null
            }
        }
    }

}
