goog.provide('cz.payola.web.client.graph.Graph');
goog.require('cz.payola.web.client.graph.Constants');
cz.payola.web.client.graph.Graph = function(vertices, edges) {
var self = this;
self.vertices = vertices;
self.edges = edges;
self.selectedVertexCount = 0;
};
cz.payola.web.client.graph.Graph.prototype.setVertexSelection = function(vertex, selected) {
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
cz.payola.web.client.graph.Graph.prototype.selectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, true);
};
cz.payola.web.client.graph.Graph.prototype.deselectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, false);
};
cz.payola.web.client.graph.Graph.prototype.invertVertexSelection = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, (! vertex.selected));
};
cz.payola.web.client.graph.Graph.prototype.deselectAll = function(graph) {
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
cz.payola.web.client.graph.Graph.prototype.containsSelectedVertex = function() {
var self = this;
return (self.selectedVertexCount > 0);
};
cz.payola.web.client.graph.Graph.prototype.getTouchedVertex = function(p) {
var self = this;
return self.vertices.find(function(v) { return self.isPointInRect(p, v.position.$plus(cz.payola.web.client.graph.Constants.VertexSize.$div(-2.0)), v.position.$plus(cz.payola.web.client.graph.Constants.VertexSize.$div(2.0)));
 });
};
cz.payola.web.client.graph.Graph.prototype.isPointInRect = function(p, topLeft, bottomRight) {
var self = this;
return (p.$greater$eq(topLeft) && p.$less$eq(bottomRight));
};
cz.payola.web.client.graph.Graph.prototype.exists = function(value) {
var self = this;
return self.vertices.exists(function($x$1) { return ($x$1.id == value.id);
 });
};
cz.payola.web.client.graph.Graph.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Graph', []);
