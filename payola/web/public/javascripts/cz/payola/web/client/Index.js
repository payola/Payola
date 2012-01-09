goog.provide('cz.payola.web.client.Index');
goog.require('cz.payola.web.client.Layer');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.Vector');
goog.require('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.graph.Edge');
goog.require('cz.payola.web.client.graph.Graph');
goog.require('cz.payola.web.client.graph.Vertex');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('scala.None');
goog.require('scala.Some');
goog.require('scala.collection.immutable.List');
cz.payola.web.client.Index.graph = null;
cz.payola.web.client.Index.drawer = null;
cz.payola.web.client.Index.selectionStart = scala.None;
cz.payola.web.client.Index.moveStart = scala.None;
cz.payola.web.client.Index.init = function() {
var self = this;
var edgesLayer = self.createLayer();
var verticesLayer = self.createLayer();
var textLayer = self.createLayer();
self.drawer = new cz.payola.web.client.graph.Drawer(edgesLayer, verticesLayer, textLayer);
var mouseLayer = self.createLayer();
goog.events.listen(mouseLayer.canvas, goog.events.EventType.MOUSEDOWN, function($event) { self.onMouseDown($event);
 });
goog.events.listen(mouseLayer.canvas, goog.events.EventType.MOUSEMOVE, function($event) { self.onMouseMove($event);
 });
goog.events.listen(mouseLayer.canvas, goog.events.EventType.MOUSEUP, function($event) { self.onMouseUp($event);
 });
self.initGraph();
self.drawer.redraw(self.graph);
};
cz.payola.web.client.Index.createLayer = function() {
var self = this;
var canvas = document.createElement('canvas');
var context = canvas.getContext('2d');
var layer = new cz.payola.web.client.Layer(canvas, context);
document.getElementById('canvas-holder').appendChild(canvas);
layer.setSize(new cz.payola.web.client.Vector(window.innerWidth, window.innerHeight));
return layer;
};
cz.payola.web.client.Index.initGraph = function() {
var self = this;
var v0 = new cz.payola.web.client.graph.Vertex(0, new cz.payola.web.client.Point(15.0, 15.0), '0');
var v1 = new cz.payola.web.client.graph.Vertex(1, new cz.payola.web.client.Point(120.0, 40.0), '1');
var v2 = new cz.payola.web.client.graph.Vertex(2, new cz.payola.web.client.Point(50.0, 120.0), '2');
var v3 = new cz.payola.web.client.graph.Vertex(3, new cz.payola.web.client.Point(180.0, 60.0), '3');
var v4 = new cz.payola.web.client.graph.Vertex(4, new cz.payola.web.client.Point(240.0, 110.0), '4');
var v5 = new cz.payola.web.client.graph.Vertex(5, new cz.payola.web.client.Point(160.0, 160.0), '5');
var v6 = new cz.payola.web.client.graph.Vertex(6, new cz.payola.web.client.Point(240.0, 240.0), '6');
var v7 = new cz.payola.web.client.graph.Vertex(7, new cz.payola.web.client.Point(270.0, 320.0), '7');
var v8 = new cz.payola.web.client.graph.Vertex(8, new cz.payola.web.client.Point(160.0, 240.0), '8');
var v9 = new cz.payola.web.client.graph.Vertex(9, new cz.payola.web.client.Point(120.0, 400.0), '9');
var v10 = new cz.payola.web.client.graph.Vertex(10, new cz.payola.web.client.Point(300.0, 80.0), '10');
var v11 = new cz.payola.web.client.graph.Vertex(11, new cz.payola.web.client.Point(320.0, 30.0), '11');
var v12 = new cz.payola.web.client.graph.Vertex(12, new cz.payola.web.client.Point(300.0, 200.0), '12');
var v13 = new cz.payola.web.client.graph.Vertex(13, new cz.payola.web.client.Point(350.0, 210.0), '13');
var v14 = new cz.payola.web.client.graph.Vertex(14, new cz.payola.web.client.Point(300.0, 400.0), '14');
var v15 = new cz.payola.web.client.graph.Vertex(15, new cz.payola.web.client.Point(80.0, 310.0), '15');
var v16 = new cz.payola.web.client.graph.Vertex(16, new cz.payola.web.client.Point(15.0, 240.0), '16');
var v17 = new cz.payola.web.client.graph.Vertex(17, new cz.payola.web.client.Point(15.0, 300.0), '17');
var v18 = new cz.payola.web.client.graph.Vertex(18, new cz.payola.web.client.Point(400.0, 15.0), '18');
var v19 = new cz.payola.web.client.graph.Vertex(19, new cz.payola.web.client.Point(400.0, 120.0), '19');
var vertices = scala.collection.immutable.List.$apply(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
var edges = scala.collection.immutable.List.$apply(new cz.payola.web.client.graph.Edge(v0, v1), new cz.payola.web.client.graph.Edge(v0, v2), new cz.payola.web.client.graph.Edge(v0, v9), new cz.payola.web.client.graph.Edge(v0, v11), new cz.payola.web.client.graph.Edge(v0, v16), new cz.payola.web.client.graph.Edge(v1, v5), new cz.payola.web.client.graph.Edge(v1, v6), new cz.payola.web.client.graph.Edge(v2, v3), new cz.payola.web.client.graph.Edge(v2, v5), new cz.payola.web.client.graph.Edge(v2, v6), new cz.payola.web.client.graph.Edge(v2, v8), new cz.payola.web.client.graph.Edge(v3, v4), new cz.payola.web.client.graph.Edge(v3, v5), new cz.payola.web.client.graph.Edge(v3, v11), new cz.payola.web.client.graph.Edge(v4, v7), new cz.payola.web.client.graph.Edge(v4, v8), new cz.payola.web.client.graph.Edge(v4, v11), new cz.payola.web.client.graph.Edge(v5, v6), new cz.payola.web.client.graph.Edge(v5, v12), new cz.payola.web.client.graph.Edge(v6, v7), new cz.payola.web.client.graph.Edge(v6, v9), new cz.payola.web.client.graph.Edge(v7, v9), new cz.payola.web.client.graph.Edge(v8, v9), new cz.payola.web.client.graph.Edge(v8, v16), new cz.payola.web.client.graph.Edge(v9, v10), new cz.payola.web.client.graph.Edge(v9, v13), new cz.payola.web.client.graph.Edge(v9, v15), new cz.payola.web.client.graph.Edge(v10, v11), new cz.payola.web.client.graph.Edge(v10, v12), new cz.payola.web.client.graph.Edge(v11, v13), new cz.payola.web.client.graph.Edge(v11, v18), new cz.payola.web.client.graph.Edge(v11, v19), new cz.payola.web.client.graph.Edge(v12, v13), new cz.payola.web.client.graph.Edge(v13, v14), new cz.payola.web.client.graph.Edge(v13, v19), new cz.payola.web.client.graph.Edge(v15, v16), new cz.payola.web.client.graph.Edge(v15, v17), new cz.payola.web.client.graph.Edge(v16, v17));
self.graph = new cz.payola.web.client.graph.Graph(vertices, edges);
};
cz.payola.web.client.Index.onMouseDown = function(event) {
var self = this;
var position = new cz.payola.web.client.Point(event.clientX, event.clientY);
var vertex = self.graph.getTouchedVertex(position);
var needsToRedraw = false;
(function() {
if (vertex.isDefined()) {
(function() {
if (event.shiftKey) {
needsToRedraw = (self.graph.invertVertexSelection(vertex.get()) || needsToRedraw);
} else {
(function() {
if ((! vertex.get().selected)) {
needsToRedraw = self.graph.deselectAll(self.graph);
} else {
}})();
self.moveStart = new scala.Some(position);
needsToRedraw = (self.graph.selectVertex(vertex.get()) || needsToRedraw);
}})();
} else {
(function() {
if ((! event.shiftKey)) {
needsToRedraw = self.graph.deselectAll(self.graph);
} else {
}})();
self.selectionStart = new scala.Some(position);
}})();
(function() {
if (needsToRedraw) {
self.drawer.redraw(self.graph);
} else {
}})();
};
cz.payola.web.client.Index.onMouseMove = function(event) {
var self = this;
(function() {
if (self.selectionStart.isDefined()) {
} else {
(function() {
if (self.moveStart.isDefined()) {
var end = new cz.payola.web.client.Point(event.clientX, event.clientY);
var difference = end.$minus(self.moveStart.get());
self.graph.vertices.foreach(function(vertex) { (function() {
if (vertex.selected) {
vertex.position = vertex.position.$plus(difference);
} else {
}})();
 });
self.moveStart = new scala.Some(end);
self.drawer.redraw(self.graph);
} else {
}})();
}})();
};
cz.payola.web.client.Index.onMouseUp = function(event) {
var self = this;
self.selectionStart = scala.None;
self.moveStart = scala.None;
self.drawer.redraw(self.graph);
};
cz.payola.web.client.Index.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Index', []);
