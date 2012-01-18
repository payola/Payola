goog.provide('cz.payola.web.client.graph.views.GraphView');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('cz.payola.web.client.graph.views.EdgeView');
goog.require('cz.payola.web.client.graph.views.VertexView');
goog.require('scala.collection.immutable.List');
goog.require('scala.collection.immutable.Nil');
cz.payola.web.client.graph.views.GraphView = function(graphModel, vertices, edges) {
var self = this;
self.graphModel = graphModel;
self.vertices = vertices;
self.edges = edges;
self.selectedVertexCount = 0;
};
cz.payola.web.client.graph.views.GraphView.prototype.init = function() {
var self = this;
self.vertices = self.createVertices(self.graphModel);
self.edges = self.createEdges(self.graphModel, self.vertices);
};
cz.payola.web.client.graph.views.GraphView.prototype.createVertices = function(graphModel) {
var self = this;
var graphWidth = 200;
var verticesView = scala.collection.immutable.Nil;
var counter = 0;
graphModel.vertices.foreach(function(vertexModel) { var point = new cz.payola.web.client.Point(((counter * 10) % graphWidth), (counter / graphWidth));
var vertexView = new cz.payola.web.client.graph.views.VertexView(counter, point, vertexModel.uri);
verticesView = var $x$1 = verticesView;
scala.collection.immutable.List.$apply(vertexView).$colon$colon$colon($x$1);
;
counter = (counter + 1);
 });
return verticesView;
};
cz.payola.web.client.graph.views.GraphView.prototype.createEdges = function(graphModel, verticesView) {
var self = this;
var edgesView = scala.collection.immutable.Nil;
graphModel.edges.foreach(function(edgeModel) { edgesView = var $x$2 = edgesView;
scala.collection.immutable.List.$apply(new cz.payola.web.client.graph.views.EdgeView(self.lookup(edgeModel.from.uri), self.lookup(edgeModel.to.uri), edgeModel.uri)).$colon$colon$colon($x$2);
;
 });
return edgesView;
};
cz.payola.web.client.graph.views.GraphView.prototype.lookup = function(uri) {
var self = this;
var result = self.vertices.head();
self.vertices.foreach(function(vertex) { (function() {
if ((vertex.information.text == uri)) {
result = vertex;
} else {
}})();
 });
return result;
};
cz.payola.web.client.graph.views.GraphView.prototype.setVertexSelection = function(vertex, selected) {
var self = this;
return (function() {
if ((vertex.selected != selected)) {
self.selectedVertexCount = (self.selectedVertexCount + (function() {
if (selected) {
return 1;
} else {
return -1;
}})());
vertex.selected = selected;
return true;
} else {
return false;
}})();
};
cz.payola.web.client.graph.views.GraphView.prototype.selectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, true);
};
cz.payola.web.client.graph.views.GraphView.prototype.deselectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, false);
};
cz.payola.web.client.graph.views.GraphView.prototype.invertVertexSelection = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, (! vertex.selected));
};
cz.payola.web.client.graph.views.GraphView.prototype.deselectAll = function(graph) {
var self = this;
var somethingChanged = false;
(function() {
if ((self.selectedVertexCount > 0)) {
self.vertices.foreach(function(vertex) { somethingChanged = (self.deselectVertex(vertex) || somethingChanged);
 });
} else {
}})();
return somethingChanged;
};
cz.payola.web.client.graph.views.GraphView.prototype.containsSelectedVertex = function() {
var self = this;
return (self.selectedVertexCount > 0);
};
cz.payola.web.client.graph.views.GraphView.prototype.getTouchedVertex = function(p) {
var self = this;
return self.vertices.find(function(v) { return self.isPointInRect(p, v.position.$plus(cz.payola.web.client.graph.Constants.VertexSize.$div(-2.0)), v.position.$plus(cz.payola.web.client.graph.Constants.VertexSize.$div(2.0)));
 });
};
cz.payola.web.client.graph.views.GraphView.prototype.isPointInRect = function(p, topLeft, bottomRight) {
var self = this;
return (p.$greater$eq(topLeft) && p.$less$eq(bottomRight));
};
cz.payola.web.client.graph.views.GraphView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.views.GraphView', []);
