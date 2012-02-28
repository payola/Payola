package cz.payola.common.rdf

import collection.immutable
import cz.payola.common.rdf
import annotation.target.field
import cz.payola.scala2json.annotations.JSONConcreteArrayClass._
import cz.payola.scala2json.annotations.{JSONConcreteArrayClass, JSONPoseableClass}

@JSONPoseableClass(otherClassName = "cz.payola.common.rdf.generic.Graph")
class GraphImpl(@(JSONConcreteArrayClass @field)(className = "scala.collection.immutable.List")val vertices: immutable.List[IdentifiedVertexImpl], @(JSONConcreteArrayClass @field)(className = "scala.collection.immutable.List")val edges: immutable.List[EdgeImpl]) extends rdf.generic.Graph {

    type EdgeType = EdgeImpl
    type VertexType = IdentifiedVertexImpl

}