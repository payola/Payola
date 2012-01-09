goog.provide('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('scala.collection.immutable.List');
cz.payola.web.client.graph.Drawer = function(layerEdges, layerVertices, layerText) {
var self = this;
self.layerEdges = layerEdges;
self.layerVertices = layerVertices;
self.layerText = layerText;
self.layers = scala.collection.immutable.List.$apply(self.layerEdges, self.layerVertices, self.layerText);
};
cz.payola.web.client.graph.Drawer.prototype.drawGraph = function(graph) {
var self = this;
graph.vertices.foreach(function(vertex) { var color = (function() {
if (vertex.selected) {
return cz.payola.web.client.graph.Constants.ColorVertexHigh;
} else {
return (function() {
if (graph.edges.exists(function(e) { return (((e.vertexA === vertex) && e.vertexB.selected) || ((e.vertexB === vertex) && e.vertexA.selected));
 })) {
return cz.payola.web.client.graph.Constants.ColorVertexMedium;
} else {
return (function() {
if (graph.containsSelectedVertex()) {
return cz.payola.web.client.graph.Constants.ColorVertexDefault;
} else {
return cz.payola.web.client.graph.Constants.ColorVertexLow;
}})();
}})();
}})();
vertex.draw(self.layerVertices.context, color);
vertex.information.draw(self.layerText.context);
 });
graph.edges.foreach(function($x$1) { $x$1.draw(self.layerEdges.context);
 });
};
cz.payola.web.client.graph.Drawer.prototype.clear = function(context, topLeft, size) {
var self = this;
var bottomRight = topLeft.$plus(size);
context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
};
cz.payola.web.client.graph.Drawer.prototype.redraw = function(graph) {
var self = this;
self.layers.foreach(function(layer) { self.clear(layer.context, cz.payola.web.client.Point.Zero, layer.getSize());
 });
self.drawGraph(graph);
};
cz.payola.web.client.graph.Drawer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Drawer', []);
