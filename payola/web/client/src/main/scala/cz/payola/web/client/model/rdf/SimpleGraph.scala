package cz.payola.web.client.model.graph

import scala.collection.immutable
import cz.payola.common.rdf.Vertex
import cz.payola.common.rdf.generic.{Graph, Edge}

// TODO just temporary definition until the server side starts sending us instances of the Vertex, Edge and Graph
// traits. Then there won't be any usage of these simple classes.

class SimpleVertex(val uri: String) extends Vertex

class SimpleEdge(val uri: String, val origin: SimpleVertex, val destination: SimpleVertex) extends Edge[SimpleVertex]

class SimpleGraph(val vertices: immutable.List[SimpleVertex],
    val edges: immutable.List[SimpleEdge]) extends Graph[SimpleVertex, SimpleEdge]
