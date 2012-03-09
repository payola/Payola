package cz.payola.data.model.graph

import cz.payola.scala2json.annotations.{JSONTransient, JSONPoseableClass}

@JSONPoseableClass(otherClass = classOf[cz.payola.common.rdf.Vertex])
class RDFNode extends cz.payola.common.rdf.Vertex {
    @JSONTransient var objectID: Int = -1
}
