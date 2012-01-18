goog.provide('cz.payola.web.client.views.graph.GraphView');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Layer');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.Vector');
goog.require('cz.payola.web.client.views.graph.EdgeView');
goog.require('cz.payola.web.client.views.graph.VertexView');
goog.require('scala.collection.immutable.List');
goog.require('scala.collection.mutable.ListBuffer');
cz.payola.web.client.views.graph.GraphView = function(graphModel, container) {
var self = this;
self.graphModel = graphModel;
self.container = container;
self.selectedVertexCount = 0;
self.vertexViews = self.createVertexViews();
self.edgeViews = self.createEdges();
self.edgesLayer = self.createLayer();
self.verticesLayer = self.createLayer();
self.textLayer = self.createLayer();
self.layers = scala.collection.immutable.List.$apply(self.edgesLayer, self.verticesLayer, self.textLayer);
};
cz.payola.web.client.views.graph.GraphView.prototype.createVertexViews = function() {
var self = this;
var graphWidth = 200;
var buffer = scala.collection.mutable.ListBuffer.$apply();
var counter = 0;
self.graphModel.vertices.foreach(function(vertexModel) { var point = new cz.payola.web.client.views.Point(((counter * 10) % graphWidth), (counter / graphWidth));
buffer.$plus$eq(new cz.payola.web.client.views.graph.VertexView(vertexModel, point));
counter = (counter + 1);
 });
return buffer;
};
cz.payola.web.client.views.graph.GraphView.prototype.createEdges = function() {
var self = this;
var buffer = scala.collection.mutable.ListBuffer.$apply();
self.graphModel.edges.foreach(function(edgeModel) { return buffer.$plus$eq(new cz.payola.web.client.views.graph.EdgeView(edgeModel, self.findVertexView(edgeModel.origin), self.findVertexView(edgeModel.destination)));
 });
return buffer;
};
cz.payola.web.client.views.graph.GraphView.prototype.findVertexView = function(vertexModel) {
var self = this;
return self.vertexViews.find(function($x$1) { return ($x$1.vertexModel === vertexModel);
 }).get();
};
cz.payola.web.client.views.graph.GraphView.prototype.setVertexSelection = function(vertex, selected) {
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
cz.payola.web.client.views.graph.GraphView.prototype.selectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, true);
};
cz.payola.web.client.views.graph.GraphView.prototype.deselectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, false);
};
cz.payola.web.client.views.graph.GraphView.prototype.invertVertexSelection = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, (! vertex.selected));
};
cz.payola.web.client.views.graph.GraphView.prototype.deselectAll = function(graph) {
var self = this;
var somethingChanged = false;
(function() {
if ((self.selectedVertexCount > 0)) {
self.vertexViews.foreach(function(vertex) { somethingChanged = (self.deselectVertex(vertex) || somethingChanged);
 });
} else {
}})();
return somethingChanged;
};
cz.payola.web.client.views.graph.GraphView.prototype.getTouchedVertex = function(p) {
var self = this;
return self.vertexViews.find(function(v) { return self.isPointInRect(p, v.position.$plus(cz.payola.web.client.views.Constants.VertexSize.$div(-2.0)), v.position.$plus(cz.payola.web.client.views.Constants.VertexSize.$div(2.0)));
 });
};
cz.payola.web.client.views.graph.GraphView.prototype.isPointInRect = function(p, topLeft, bottomRight) {
var self = this;
return (p.$greater$eq(topLeft) && p.$less$eq(bottomRight));
};
cz.payola.web.client.views.graph.GraphView.prototype.createLayer = function() {
var self = this;
var canvas = document.createElement('canvas');
var context = canvas.getContext('2d');
var layer = new cz.payola.web.client.views.Layer(canvas, context);
self.container.appendChild(canvas);
layer.setSize(new cz.payola.web.client.views.Vector(window.innerWidth, window.innerHeight));
return layer;
};
cz.payola.web.client.views.graph.GraphView.prototype.draw = function() {
var self = this;
self.vertexViews.foreach(function(vertexView) { var color = (function() {
if (vertexView.selected) {
return cz.payola.web.client.views.Constants.ColorVertexHigh;
} else {
return (function() {
if (self.edgeViews.exists(function(edgeView) { return (((edgeView.originView === vertexView) && edgeView.destinationView.selected) || ((edgeView.destinationView === vertexView) && edgeView.originView.selected));
 })) {
return cz.payola.web.client.views.Constants.ColorVertexMedium;
} else {
return (function() {
if ((self.selectedVertexCount > 0)) {
return cz.payola.web.client.views.Constants.ColorVertexDefault;
} else {
return cz.payola.web.client.views.Constants.ColorVertexLow;
}})();
}})();
}})();
vertexView.draw(self.verticesLayer.context, color);
vertexView.information.draw(self.textLayer.context);
 });
self.edgeViews.foreach(function($x$2) { $x$2.draw(self.edgesLayer.context);
 });
};
cz.payola.web.client.views.graph.GraphView.prototype.clear = function(context, topLeft, size) {
var self = this;
var bottomRight = topLeft.$plus(size);
context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
};
cz.payola.web.client.views.graph.GraphView.prototype.redraw = function() {
var self = this;
self.layers.foreach(function(layer) { self.clear(layer.context, cz.payola.web.client.views.Point.Zero, layer.getSize());
 });
self.draw();
};
cz.payola.web.client.views.graph.GraphView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.GraphView', []);
