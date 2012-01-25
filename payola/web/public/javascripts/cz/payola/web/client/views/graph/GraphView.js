goog.provide('cz.payola.web.client.views.graph.GraphView');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Layer');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.Vector');
goog.require('cz.payola.web.client.views.graph.Controls');
goog.require('cz.payola.web.client.views.graph.EdgeView');
goog.require('cz.payola.web.client.views.graph.LocationDescriptor');
goog.require('cz.payola.web.client.views.graph.RedrawOperation');
goog.require('cz.payola.web.client.views.graph.VertexView');
goog.require('cz.payola.web.client.views.graph.View');
goog.require('scala.collection.immutable.List');
goog.require('scala.collection.mutable.ListBuffer');
goog.require('scala.math');
cz.payola.web.client.views.graph.GraphView = function(graphModel, container) {
var self = this;
self.graphModel = graphModel;
self.container = container;
self.edgesDeselectedLayer = self.createLayer(self.container);
self.edgesDeselectedTextLayer = self.createLayer(self.container);
self.edgesSelectedLayer = self.createLayer(self.container);
self.edgesSelectedTextLayer = self.createLayer(self.container);
self.verticesDeselectedLayer = self.createLayer(self.container);
self.verticesDeselectedTextLayer = self.createLayer(self.container);
self.verticesSelectedLayer = self.createLayer(self.container);
self.verticesSelectedTextLayer = self.createLayer(self.container);
self.topBlankLayer = self.createLayer(self.container);
self.layers = scala.collection.immutable.List.$apply(self.edgesDeselectedLayer, self.edgesDeselectedTextLayer, self.edgesSelectedLayer, self.edgesSelectedTextLayer, self.verticesDeselectedLayer, self.verticesDeselectedTextLayer, self.verticesSelectedLayer, self.verticesSelectedTextLayer, self.topBlankLayer);
self.controlsLayer = new cz.payola.web.client.views.graph.Controls(self, self.topBlankLayer);
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
var graphWidth = 500;
var buffer = scala.collection.mutable.ListBuffer.$apply();
var counter = 0;
var spaceBetweenVertices = 80;
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
(function() {
if (vertexView.selected) {
(function() {
if (self.verticesSelectedLayer.cleared) {
vertexView.draw(self.verticesSelectedLayer.context, colorToUse, position);
} else {
}})();
(function() {
if (self.verticesSelectedTextLayer.cleared) {
vertexView.information.draw(self.verticesSelectedTextLayer.context, color, cz.payola.web.client.views.graph.LocationDescriptor.getVertexInformationPosition(vertexView.position).$plus(positionCorrection));
} else {
}})();
} else {
(function() {
if (self.verticesDeselectedLayer.cleared) {
vertexView.draw(self.verticesDeselectedLayer.context, colorToUse, position);
} else {
}})();
(function() {
if (self.verticesDeselectedTextLayer.cleared) {
vertexView.information.draw(self.verticesDeselectedTextLayer.context, color, cz.payola.web.client.views.graph.LocationDescriptor.getVertexInformationPosition(vertexView.position).$plus(positionCorrection));
} else {
}})();
}})();
 });
self.edgeViews.foreach(function(edgeView) { var positionToUse = cz.payola.web.client.views.graph.LocationDescriptor.getEdgeInformationPosition(edgeView.originView.position, edgeView.destinationView.position).$plus(positionCorrection);
var colorToUse = (function() {
if ((color != null)) {
return color;
} else {
return (function() {
if (edgeView.isSelected()) {
return cz.payola.web.client.views.Constants.ColorEdgeSelect;
} else {
return cz.payola.web.client.views.Constants.ColorEdge;
}})();
}})();
(function() {
if (edgeView.isSelected()) {
(function() {
if (self.edgesSelectedLayer.cleared) {
edgeView.draw(self.edgesSelectedLayer.context, color, position);
} else {
}})();
(function() {
if (self.edgesSelectedTextLayer.cleared) {
edgeView.information.draw(self.edgesSelectedTextLayer.context, colorToUse, positionToUse);
} else {
}})();
} else {
(function() {
if (self.edgesDeselectedLayer.cleared) {
edgeView.draw(self.edgesDeselectedLayer.context, color, position);
} else {
}})();
(function() {
if (self.edgesDeselectedTextLayer.cleared) {
edgeView.information.draw(self.edgesDeselectedTextLayer.context, colorToUse, positionToUse);
} else {
}})();
}})();
 });
self.layers.foreach(function(layer) { layer.cleared = false;
 });
};
cz.payola.web.client.views.graph.GraphView.prototype.redraw = function(graphOperation) {
var self = this;
(function($selector_1) {
if ($selector_1 === cz.payola.web.client.views.graph.RedrawOperation.Movement) {
self.clear(self.edgesSelectedLayer.context, cz.payola.web.client.views.Point.Zero, self.edgesSelectedLayer.getSize());
self.edgesSelectedLayer.cleared = true;
self.clear(self.edgesSelectedTextLayer.context, cz.payola.web.client.views.Point.Zero, self.edgesSelectedTextLayer.getSize());
self.edgesSelectedTextLayer.cleared = true;
self.clear(self.verticesSelectedLayer.context, cz.payola.web.client.views.Point.Zero, self.verticesSelectedLayer.getSize());
self.verticesSelectedLayer.cleared = true;
self.clear(self.verticesSelectedTextLayer.context, cz.payola.web.client.views.Point.Zero, self.verticesSelectedTextLayer.getSize());
self.verticesSelectedTextLayer.cleared = true;
self.draw(null, null, null);
return;
}
if ($selector_1 === cz.payola.web.client.views.graph.RedrawOperation.Selection) {
self.redrawAll();
return;
}
if (true) {
self.redrawAll();
return;
}
})(graphOperation);
};
cz.payola.web.client.views.graph.GraphView.prototype.redrawAll = function() {
var self = this;
self.layers.foreach(function(layer) { self.clear(layer.context, cz.payola.web.client.views.Point.Zero, layer.getSize());
layer.cleared = true;
 });
self.draw(null, null, null);
};
cz.payola.web.client.views.graph.GraphView.prototype.clear = function(context, topLeft, size) {
var self = this;
var bottomRight = topLeft.$plus(size);
context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
};
cz.payola.web.client.views.graph.GraphView.prototype.createLayer = function(container) {
var self = this;
var canvas = document.createElement('canvas');
var context = canvas.getContext('2d');
var layer = new cz.payola.web.client.views.Layer(canvas, context);
container.appendChild(canvas);
layer.setSize(new cz.payola.web.client.views.Vector(600.0, 500.0));
return layer;
};
cz.payola.web.client.views.graph.GraphView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.GraphView', [cz.payola.web.client.views.graph.View]);
