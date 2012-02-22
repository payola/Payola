package cz.payola.web.client.model.graph

import scala.collection.immutable
import cz.payola.common.rdf.generic.{Graph, Edge}
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}

// TODO just temporary definition until the server side starts sending us instances of the Vertex, Edge and Graph
// traits. Then there won't be any usage of these simple classes.

class SimpleVertex extends Vertex

class SimpleIdentifiedVertex(val uri: String) extends SimpleVertex with IdentifiedVertex

class SimpleEdge(val uri: String, val origin: SimpleEdge#IdentifiedVertexType, val destination: SimpleEdge#VertexType)
    extends Edge
{
    type VertexType = SimpleVertex
    
    type IdentifiedVertexType = SimpleIdentifiedVertex
}

class SimpleGraph(val vertices: immutable.List[SimpleVertex],
    val edges: immutable.List[SimpleEdge]) extends Graph
{
    type EdgeType = SimpleEdge
}
