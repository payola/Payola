goog.provide('cz.payola.web.client.Index');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.graph.Vertex');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('scala.collection.immutable.List');
cz.payola.web.client.Index.graph = null;
cz.payola.web.client.Index.context = null;
cz.payola.web.client.Index.drawer = null;
cz.payola.web.client.Index.selectedVertexCount = 0;
cz.payola.web.client.Index.init = function() {
var self = this;
var canvas = document.createElement('canvas');
self.context = canvas.getContext('2d');
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;
document.getElementById('canvas-holder').appendChild(canvas);
self.drawer = new cz.payola.web.client.graph.Drawer(self.context);
goog.events.listen(canvas, goog.events.EventType.MOUSEDOWN, function($e) { self.onMouseDown($e);
 });
self.graph = self.getGraph();
self.drawer.redraw(self.graph);
};
cz.payola.web.client.Index.getGraph = function() {
var self = this;
var v0 = new cz.payola.web.client.graph.Vertex(0, 15.0, 15.0, '0', null);
var v1 = new cz.payola.web.client.graph.Vertex(1, 120.0, 40.0, '1', null);
var v2 = new cz.payola.web.client.graph.Vertex(2, 50.0, 120.0, '2', null);
var v3 = new cz.payola.web.client.graph.Vertex(3, 180.0, 60.0, '3', null);
var v4 = new cz.payola.web.client.graph.Vertex(4, 240.0, 110.0, '4', null);
var v5 = new cz.payola.web.client.graph.Vertex(5, 160.0, 160.0, '5', null);
var v6 = new cz.payola.web.client.graph.Vertex(6, 240.0, 240.0, '6', null);
var v7 = new cz.payola.web.client.graph.Vertex(7, 270.0, 320.0, '7', null);
var v8 = new cz.payola.web.client.graph.Vertex(8, 160.0, 240.0, '8', null);
var v9 = new cz.payola.web.client.graph.Vertex(9, 120.0, 400.0, '9', null);
var v10 = new cz.payola.web.client.graph.Vertex(10, 300.0, 80.0, '10', null);
var v11 = new cz.payola.web.client.graph.Vertex(11, 320.0, 30.0, '11', null);
var v12 = new cz.payola.web.client.graph.Vertex(12, 300.0, 200.0, '12', null);
var v13 = new cz.payola.web.client.graph.Vertex(13, 350.0, 210.0, '13', null);
var v14 = new cz.payola.web.client.graph.Vertex(14, 300.0, 400.0, '14', null);
var v15 = new cz.payola.web.client.graph.Vertex(15, 80.0, 310.0, '15', null);
var v16 = new cz.payola.web.client.graph.Vertex(16, 15.0, 240.0, '16', null);
var v17 = new cz.payola.web.client.graph.Vertex(17, 15.0, 300.0, '17', null);
var v18 = new cz.payola.web.client.graph.Vertex(18, 400.0, 15.0, '18', null);
var v19 = new cz.payola.web.client.graph.Vertex(19, 400.0, 120.0, '19', null);
v0.neighbours = scala.collection.immutable.List.$apply(v1, v2, v9, v11, v16);
v1.neighbours = scala.collection.immutable.List.$apply(v0, v5, v6);
v2.neighbours = scala.collection.immutable.List.$apply(v0, v3, v5, v6, v8);
v3.neighbours = scala.collection.immutable.List.$apply(v2, v4, v5, v11);
v4.neighbours = scala.collection.immutable.List.$apply(v3, v7, v8, v11);
v5.neighbours = scala.collection.immutable.List.$apply(v1, v2, v3, v6, v12);
v6.neighbours = scala.collection.immutable.List.$apply(v1, v2, v5, v7, v9);
v7.neighbours = scala.collection.immutable.List.$apply(v4, v6, v9);
v8.neighbours = scala.collection.immutable.List.$apply(v2, v4, v9, v16);
v9.neighbours = scala.collection.immutable.List.$apply(v0, v6, v7, v8, v10, v13, v15);
v10.neighbours = scala.collection.immutable.List.$apply(v9, v11, v12);
v11.neighbours = scala.collection.immutable.List.$apply(v0, v3, v4, v10, v13, v18, v19);
v12.neighbours = scala.collection.immutable.List.$apply(v5, v10, v13);
v13.neighbours = scala.collection.immutable.List.$apply(v9, v11, v12, v14, v19);
v14.neighbours = scala.collection.immutable.List.$apply(v13);
v15.neighbours = scala.collection.immutable.List.$apply(v9, v16, v17);
v16.neighbours = scala.collection.immutable.List.$apply(v0, v8, v15, v17);
v17.neighbours = scala.collection.immutable.List.$apply(v15, v16);
v18.neighbours = scala.collection.immutable.List.$apply(v11);
v19.neighbours = scala.collection.immutable.List.$apply(v11, v13);
return scala.collection.immutable.List.$apply(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
};
cz.payola.web.client.Index.onMouseDown = function(e) {
var self = this;
var needsToRedraw = false;
var touchedVertex = self.getTouchedVertex(e.clientX, e.clientY);
(function() {
if ((! e.shiftKey)) {
needsToRedraw = self.deselectAll(self.graph);
} else {
}})();
(function() {
if (touchedVertex.isDefined()) {
(function() {
if (e.shiftKey) {
needsToRedraw = self.invertVertexSelection(touchedVertex.get());
} else {
needsToRedraw = self.selectVertex(touchedVertex.get());
}})();
} else {
}})();
(function() {
if (needsToRedraw) {
self.drawer.redraw(self.graph);
} else {
}})();
};
cz.payola.web.client.Index.getTouchedVertex = function(x, y) {
var self = this;
return self.graph.find(function(vertex) { return self.isPointInRect(x, y, (vertex.x - (cz.payola.web.client.graph.Constants.VERTEX_WIDTH / 2)), (vertex.y - (cz.payola.web.client.graph.Constants.VERTEX_HEIGHT / 2)), cz.payola.web.client.graph.Constants.VERTEX_WIDTH, cz.payola.web.client.graph.Constants.VERTEX_HEIGHT);
 });
};
cz.payola.web.client.Index.isPointInRect = function(x, y, rectX, rectY, rectW, rectH) {
var self = this;
return ((((x >= rectX) && (x <= (rectX + rectW))) && (y >= rectY)) && (y <= (rectY + rectH)));
};
cz.payola.web.client.Index.setVertexSelection = function(vertex, selected) {
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
cz.payola.web.client.Index.selectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, true);
};
cz.payola.web.client.Index.deselectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, false);
};
cz.payola.web.client.Index.invertVertexSelection = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, (! vertex.selected));
};
cz.payola.web.client.Index.deselectAll = function(graph) {
var self = this;
var somethingChanged = false;
(function() {
if ((self.selectedVertexCount > 0)) {
graph.foreach(function(vertex) { somethingChanged = (self.deselectVertex(vertex) || somethingChanged);
 });
} else {
}})();
return somethingChanged;
};
cz.payola.web.client.Index.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Index', []);
