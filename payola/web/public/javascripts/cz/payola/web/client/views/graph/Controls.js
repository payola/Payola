goog.provide('cz.payola.web.client.views.graph.Controls');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.graph.RedrawOperation');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('scala.None');
goog.require('scala.Some');
cz.payola.web.client.views.graph.Controls = function(graphView, layer) {
var self = this;
self.graphView = graphView;
self.layer = layer;
self.selectedCount = 0;
self.selectionStart = scala.None;
self.moveStart = scala.None;
};
cz.payola.web.client.views.graph.Controls.prototype.init = function() {
var self = this;
goog.events.listen(self.layer.canvas, goog.events.EventType.MOUSEDOWN, function($event) { self.onMouseDown($event);
 });
goog.events.listen(self.layer.canvas, goog.events.EventType.MOUSEMOVE, function($event) { self.onMouseMove($event);
 });
goog.events.listen(self.layer.canvas, goog.events.EventType.MOUSEUP, function($event) { self.onMouseUp($event);
 });
};
cz.payola.web.client.views.graph.Controls.prototype.onMouseDown = function(event) {
var self = this;
var position = new cz.payola.web.client.views.Point(event.clientX, event.clientY);
var vertex = self.getTouchedVertex(position);
var needsToRedraw = false;
(function() {
if (vertex.isDefined()) {
(function() {
if (event.shiftKey) {
needsToRedraw = (self.invertVertexSelection(vertex.get()) || needsToRedraw);
} else {
(function() {
if ((! vertex.get().selected)) {
needsToRedraw = self.deselectAll(self.graphView);
} else {
}})();
self.moveStart = new scala.Some(position);
needsToRedraw = (self.selectVertex(vertex.get()) || needsToRedraw);
}})();
} else {
(function() {
if ((! event.shiftKey)) {
needsToRedraw = self.deselectAll(self.graphView);
} else {
}})();
self.selectionStart = new scala.Some(position);
}})();
(function() {
if (needsToRedraw) {
self.graphView.redraw(cz.payola.web.client.views.graph.RedrawOperation.Selection);
} else {
}})();
};
cz.payola.web.client.views.graph.Controls.prototype.onMouseMove = function(event) {
var self = this;
(function() {
if (self.moveStart.isDefined()) {
var end = new cz.payola.web.client.views.Point(event.clientX, event.clientY);
var difference = end.$minus(self.moveStart.get());
self.graphView.vertexViews.foreach(function(vertex) { (function() {
if (vertex.selected) {
vertex.position = vertex.position.$plus(difference);
} else {
}})();
 });
self.moveStart = new scala.Some(end);
self.graphView.redraw(cz.payola.web.client.views.graph.RedrawOperation.Movement);
} else {
}})();
};
cz.payola.web.client.views.graph.Controls.prototype.onMouseUp = function(event) {
var self = this;
self.selectionStart = scala.None;
self.moveStart = scala.None;
};
cz.payola.web.client.views.graph.Controls.prototype.getTouchedVertex = function(p) {
var self = this;
return self.graphView.vertexViews.find(function(v) { return self.isPointInRect(p, v.position.$plus(cz.payola.web.client.views.Constants.VertexSize.$div(-2.0)), v.position.$plus(cz.payola.web.client.views.Constants.VertexSize.$div(2.0)));
 });
};
cz.payola.web.client.views.graph.Controls.prototype.isPointInRect = function(p, topLeft, bottomRight) {
var self = this;
return (p.$greater$eq(topLeft) && p.$less$eq(bottomRight));
};
cz.payola.web.client.views.graph.Controls.prototype.setVertexSelection = function(vertex, selected) {
var self = this;
return (function() {
if ((vertex.selected != selected)) {
self.selectedCount = (self.selectedCount + (function() {
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
cz.payola.web.client.views.graph.Controls.prototype.selectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, true);
};
cz.payola.web.client.views.graph.Controls.prototype.deselectVertex = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, false);
};
cz.payola.web.client.views.graph.Controls.prototype.invertVertexSelection = function(vertex) {
var self = this;
return self.setVertexSelection(vertex, (! vertex.selected));
};
cz.payola.web.client.views.graph.Controls.prototype.deselectAll = function(graph) {
var self = this;
var somethingChanged = false;
(function() {
if ((self.selectedCount > 0)) {
self.graphView.vertexViews.foreach(function(vertex) { somethingChanged = (self.deselectVertex(vertex) || somethingChanged);
 });
self.selectedCount = 0;
} else {
}})();
return somethingChanged;
};
cz.payola.web.client.views.graph.Controls.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.Controls', []);
