package cz.payola.web.client.model.graph

import scala.collection.immutable
import cz.payola.common.rdf.{Graph, Edge}
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}

// TODO just temporary definition until the server side starts sending us parameterValues of the Vertex, Edge and Graph
// traits. Then there won't be any usage of these simple classes.

class SimpleVertex extends Vertex

class SimpleIdentifiedVertex(protected val _uri: String) extends SimpleVertex with IdentifiedVertex

class SimpleEdge(protected val _uri: String, protected val _origin: SimpleIdentifiedVertex, protected val _destination: SimpleVertex)
    extends Edge
{
    type VertexType = SimpleVertex
    
    type IdentifiedVertexType = SimpleIdentifiedVertex
}

class SimpleGraph(protected val _vertices: immutable.List[SimpleVertex], protected val _edges: immutable.List[SimpleEdge])
    extends Graph
{
    type EdgeType = SimpleEdge
}
