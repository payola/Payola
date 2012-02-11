package cz.payola.rdf2scala

import com.hp.hpl.jena.rdf.model.{StmtIterator, Resource}
import cz.payola.scala2json.traits._
import cz.payola.scala2json.annotations._
import collection.mutable._

object RDFNode {
    def apply(resource: Resource): RDFNode = {
        val node = new RDFNode(resource.getNameSpace, resource.getURI)

        val iterator: StmtIterator = resource.listProperties
        while (iterator.hasNext) {
            node.addProperty(RDFProperty(iterator.nextStatement()))
        }


        node
    }
}

class RDFNode(val namespace: String, val URI: String) extends JSONSerializationAdditionalFields {
    @JSONTransient private val _properties: ArrayBuffer[RDFProperty] = new ArrayBuffer[RDFProperty]()

    /** Adds property to properties.
     *
     * @param property The property.
     */
    private def addProperty(property: RDFProperty) = _properties += property

    /** @see JSONSerializationAdditionalFields
     *
     * @return JSON value.
     */
    def additionalFieldsForJSONSerialization: Map[String, Any] = {
        val fields = new HashMap[String, Any]()
        _properties foreach (prop => {
            fields.put(prop.name, prop.value)
        })

        fields
    }

}
