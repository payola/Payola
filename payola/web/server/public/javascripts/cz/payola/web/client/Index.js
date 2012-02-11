goog.provide('cz.payola.web.client.Index');
goog.require('cz.payola.web.client.Layer');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.graph.Graph');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('scala.None');
goog.require('scala.Some');
cz.payola.web.client.Index.graph = new cz.payola.web.client.graph.Graph(null);
cz.payola.web.client.Index.layerVertices = null;
cz.payola.web.client.Index.layerEdges = null;
cz.payola.web.client.Index.layerText = null;
cz.payola.web.client.Index.drawer = null;
cz.payola.web.client.Index.selectionStart = scala.None;
cz.payola.web.client.Index.moveStart = scala.None;
cz.payola.web.client.Index.init = function() {
var self = this;
var canvasEdges = document.createElement('canvas');
var contextEdges = canvasEdges.getContext('2d');
self.layerEdges = new cz.payola.web.client.Layer(canvasEdges, contextEdges);
self.layerEdges.setWidth(window.innerWidth);
self.layerEdges.setHeight(window.innerHeight);
document.getElementById('canvas-holder').appendChild(canvasEdges);
var canvasVertices = document.createElement('canvas');
var contextVertices = canvasVertices.getContext('2d');
self.layerVertices = new cz.payola.web.client.Layer(canvasVertices, contextVertices);
self.layerVertices.setWidth(window.innerWidth);
self.layerVertices.setHeight(window.innerHeight);
document.getElementById('canvas-holder').appendChild(canvasVertices);
var canvasText = document.createElement('canvas');
var contextText = canvasText.getContext('2d');
self.layerText = new cz.payola.web.client.Layer(canvasText, contextText);
self.layerText.setWidth(window.innerWidth);
self.layerText.setHeight(window.innerHeight);
document.getElementById('canvas-holder').appendChild(canvasText);
self.drawer = new cz.payola.web.client.graph.Drawer(self.layerEdges, self.layerVertices, self.layerText);
goog.events.listen(canvasText, goog.events.EventType.MOUSEDOWN, function($event) {
self.onMouseDown($event);
});
goog.events.listen(canvasText, goog.events.EventType.MOUSEMOVE, function($event) {
self.onMouseMove($event);
});
goog.events.listen(canvasText, goog.events.EventType.MOUSEUP, function($event) {
self.onMouseUp($event);
});
self.drawer.redraw(self.graph);

};
cz.payola.web.client.Index.onMouseDown = function(event) {
var self = this;
var position = new cz.payola.web.client.Point(event.clientX, event.clientY);
var vertex = self.graph.getTouchedVertex(position);
var needsToRedraw = false;
if (vertex.isDefined()) {
if (event.shiftKey) {
needsToRedraw = (self.graph.invertVertexSelection(vertex.get()) || needsToRedraw);
} else {
if ((! vertex.get().selected)) {
needsToRedraw = self.graph.deselectAll(self.graph);
}
self.moveStart = new scala.Some(position);
needsToRedraw = (self.graph.selectVertex(vertex.get()) || needsToRedraw);

}
} else {
if ((! event.shiftKey)) {
needsToRedraw = self.graph.deselectAll(self.graph);
}
self.selectionStart = new scala.Some(position);

}
if (needsToRedraw) {
self.drawer.redraw(self.graph);
}

};
cz.payola.web.client.Index.onMouseMove = function(event) {
var self = this;
if (self.selectionStart.isDefined()) {

} else {
if (self.moveStart.isDefined()) {
var end = new cz.payola.web.client.Point(event.clientX, event.clientY);
var difference = end.subtract(self.moveStart.get());
self.graph.getGraph().foreach(function(vertex) {
if (vertex.selected) {
vertex.position = vertex.position.add(difference);
}
});
self.moveStart = new scala.Some(end);
self.drawer.redraw(self.graph);

}
}
};
cz.payola.web.client.Index.onMouseUp = function(event) {
var self = this;
self.selectionStart = scala.None;
self.moveStart = scala.None;
self.drawer.redraw(self.graph);

};
cz.payola.web.client.Index.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Index', []);
