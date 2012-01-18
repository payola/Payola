goog.provide('cz.payola.web.client.Index');
goog.require('cz.payola.web.client.Layer');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.Vector');
goog.require('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.graph.model.Edge');
goog.require('cz.payola.web.client.graph.model.Graph');
goog.require('cz.payola.web.client.graph.model.Vertex');
goog.require('cz.payola.web.client.graph.views.EdgeView');
goog.require('cz.payola.web.client.graph.views.GraphView');
goog.require('cz.payola.web.client.graph.views.VertexView');
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
goog.events.listen(mouseLayer.canvas, goog.events.EventType.MOUSEUP, function($event) { self.onMouseUp($event);
 });
self.initGraph();
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
var v0 = new cz.payola.web.client.graph.model.Vertex('0');
var v1 = new cz.payola.web.client.graph.model.Vertex('1');
var v2 = new cz.payola.web.client.graph.model.Vertex('2');
var v3 = new cz.payola.web.client.graph.model.Vertex('3');
var v4 = new cz.payola.web.client.graph.model.Vertex('4');
var v5 = new cz.payola.web.client.graph.model.Vertex('5');
var v6 = new cz.payola.web.client.graph.model.Vertex('6');
var v7 = new cz.payola.web.client.graph.model.Vertex('7');
var v8 = new cz.payola.web.client.graph.model.Vertex('8');
var v9 = new cz.payola.web.client.graph.model.Vertex('9');
var v10 = new cz.payola.web.client.graph.model.Vertex('10');
var v11 = new cz.payola.web.client.graph.model.Vertex('11');
var v12 = new cz.payola.web.client.graph.model.Vertex('12');
var v13 = new cz.payola.web.client.graph.model.Vertex('13');
var v14 = new cz.payola.web.client.graph.model.Vertex('14');
var v15 = new cz.payola.web.client.graph.model.Vertex('15');
var v16 = new cz.payola.web.client.graph.model.Vertex('16');
var v17 = new cz.payola.web.client.graph.model.Vertex('17');
var v18 = new cz.payola.web.client.graph.model.Vertex('18');
var v19 = new cz.payola.web.client.graph.model.Vertex('19');
var vertices = scala.collection.immutable.List.$apply(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
var edges = scala.collection.immutable.List.$apply(new cz.payola.web.client.graph.model.Edge('0', v0, v1), new cz.payola.web.client.graph.model.Edge('1', v0, v2), new cz.payola.web.client.graph.model.Edge('2', v0, v9), new cz.payola.web.client.graph.model.Edge('3', v0, v11), new cz.payola.web.client.graph.model.Edge('4', v0, v16), new cz.payola.web.client.graph.model.Edge('5', v1, v5), new cz.payola.web.client.graph.model.Edge('6', v1, v6), new cz.payola.web.client.graph.model.Edge('7', v2, v3), new cz.payola.web.client.graph.model.Edge('8', v2, v5), new cz.payola.web.client.graph.model.Edge('9', v2, v6), new cz.payola.web.client.graph.model.Edge('10', v2, v8), new cz.payola.web.client.graph.model.Edge('11', v3, v4), new cz.payola.web.client.graph.model.Edge('12', v3, v5), new cz.payola.web.client.graph.model.Edge('13', v3, v11), new cz.payola.web.client.graph.model.Edge('14', v4, v7), new cz.payola.web.client.graph.model.Edge('15', v4, v8), new cz.payola.web.client.graph.model.Edge('16', v4, v11), new cz.payola.web.client.graph.model.Edge('17', v5, v6), new cz.payola.web.client.graph.model.Edge('18', v5, v12), new cz.payola.web.client.graph.model.Edge('19', v6, v7), new cz.payola.web.client.graph.model.Edge('20', v6, v9), new cz.payola.web.client.graph.model.Edge('21', v7, v9), new cz.payola.web.client.graph.model.Edge('22', v8, v9), new cz.payola.web.client.graph.model.Edge('23', v8, v16), new cz.payola.web.client.graph.model.Edge('24', v9, v10), new cz.payola.web.client.graph.model.Edge('25', v9, v13), new cz.payola.web.client.graph.model.Edge('26', v9, v15), new cz.payola.web.client.graph.model.Edge('27', v10, v11), new cz.payola.web.client.graph.model.Edge('28', v10, v12), new cz.payola.web.client.graph.model.Edge('29', v11, v13), new cz.payola.web.client.graph.model.Edge('30', v11, v18), new cz.payola.web.client.graph.model.Edge('31', v11, v19), new cz.payola.web.client.graph.model.Edge('32', v12, v13), new cz.payola.web.client.graph.model.Edge('33', v13, v14), new cz.payola.web.client.graph.model.Edge('34', v13, v19), new cz.payola.web.client.graph.model.Edge('35', v15, v16), new cz.payola.web.client.graph.model.Edge('36', v15, v17), new cz.payola.web.client.graph.model.Edge('37', v16, v17));
var graphModel = new cz.payola.web.client.graph.model.Graph(vertices, edges);
var vertexPlk1 = new cz.payola.web.client.graph.views.VertexView(0, new cz.payola.web.client.Point(1.0, 1.0), 'plk1');
var vertexPlk2 = new cz.payola.web.client.graph.views.VertexView(1, new cz.payola.web.client.Point(10.0, 10.0), 'plk1');
var edgePlk = new cz.payola.web.client.graph.views.EdgeView(vertexPlk1, vertexPlk2, 'plk');
var verticesPlk = scala.collection.immutable.List.$apply(vertexPlk1, vertexPlk2);
var edgesPlk = scala.collection.immutable.List.$apply(edgePlk);
self.graph = new cz.payola.web.client.graph.views.GraphView(graphModel, verticesPlk, edgesPlk);
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
cz.payola.web.client.Index.onMouseUp = function(event) {
var self = this;
self.selectionStart = scala.None;
self.moveStart = scala.None;
self.drawer.redraw(self.graph);
};
cz.payola.web.client.Index.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Index', []);
