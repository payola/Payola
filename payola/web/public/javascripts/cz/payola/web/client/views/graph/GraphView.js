goog.provide('cz.payola.web.client.views.graph.GraphView');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Layer');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.Vector');
goog.require('cz.payola.web.client.views.graph.Controls');
goog.require('cz.payola.web.client.views.graph.EdgeView');
goog.require('cz.payola.web.client.views.graph.LocationDescriptor');
goog.require('cz.payola.web.client.views.graph.VertexView');
goog.require('cz.payola.web.client.views.graph.View');
goog.require('scala.collection.immutable.List');
goog.require('scala.collection.mutable.ListBuffer');
goog.require('scala.math');
cz.payola.web.client.views.graph.GraphView = function(graphModel, container) {
var self = this;
self.graphModel = graphModel;
self.container = container;
self.edgesLayer = self.createLayer();
self.edgesTextLayer = self.createLayer();
self.verticesLayer = self.createLayer();
self.verticesTextLayer = self.createLayer();
self.blankLayer = self.createLayer();
self.layers = scala.collection.immutable.List.$apply(self.edgesLayer, self.edgesTextLayer, self.verticesLayer, self.verticesTextLayer, self.blankLayer);
self.controlsLayer = new cz.payola.web.client.views.graph.Controls(self, self.blankLayer);
self.vertexViews = self.createVertexViews();
self.edgeViews = self.createEdges();
goog.base(self);};
goog.inherits(cz.payola.web.client.views.graph.GraphView, cz.payola.web.client.views.graph.View);
cz.payola.web.client.views.graph.GraphView.prototype.initControls = function() {
var self = this;
self.controlsLayer.init();
};
cz.payola.web.client.views.graph.GraphView.prototype.createVertexViews = function() {
var self = this;
var graphWidth = self.verticesLayer.getSize().x;
var buffer = scala.collection.mutable.ListBuffer.$apply();
var counter = 0;
var spaceBetweenVertices = 50;
self.graphModel.vertices.foreach(function(vertexModel) { var point = new cz.payola.web.client.views.Point((((counter * spaceBetweenVertices) % graphWidth) + 20), ((scala.math.floor(((counter * spaceBetweenVertices) / graphWidth)) * spaceBetweenVertices) + 20));
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
cz.payola.web.client.views.graph.GraphView.prototype.createLayer = function() {
var self = this;
var canvas = document.createElement('canvas');
var context = canvas.getContext('2d');
var layer = new cz.payola.web.client.views.Layer(canvas, context);
self.container.appendChild(canvas);
layer.setSize(new cz.payola.web.client.views.Vector(400.0, 500.0));
return layer;
};
cz.payola.web.client.views.graph.GraphView.prototype.draw = function(context, color, position) {
var self = this;
var positionCorrection = (function() {
if ((position != null)) {
return position.toVector();
} else {
return cz.payola.web.client.views.Point.Zero.toVector();
}})();
self.vertexViews.foreach(function(vertexView) { var colorToUse = (function() {
if ((color != null)) {
return color;
} else {
return (function() {
if (vertexView.selected) {
return cz.payola.web.client.views.Constants.ColorVertexHigh;
} else {
return (function() {
if (self.edgeViews.exists(function(edgeView) { return (((edgeView.originView === vertexView) && edgeView.destinationView.selected) || ((edgeView.destinationView === vertexView) && edgeView.originView.selected));
 })) {
return cz.payola.web.client.views.Constants.ColorVertexMedium;
} else {
return (function() {
if ((self.controlsLayer.selectedCount == 0)) {
return cz.payola.web.client.views.Constants.ColorVertexDefault;
} else {
return cz.payola.web.client.views.Constants.ColorVertexLow;
}})();
}})();
}})();
}})();
vertexView.draw(self.verticesLayer.context, colorToUse, position);
vertexView.information.draw(self.verticesTextLayer.context, color, cz.payola.web.client.views.graph.LocationDescriptor.getVertexInformationPosition(vertexView.position).$plus(positionCorrection));
 });
self.edgeViews.foreach(function(edgeView) { edgeView.draw(self.edgesLayer.context, color, position);
edgeView.information.draw(self.edgesTextLayer.context, color, cz.payola.web.client.views.graph.LocationDescriptor.getEdgeInformationPosition(edgeView.originView.position, edgeView.destinationView.position).$plus(positionCorrection));
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
self.draw(null, null, null);
};
cz.payola.web.client.views.graph.GraphView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.GraphView', [cz.payola.web.client.views.graph.View]);
