goog.provide('cz.payola.web.client.graph.Drawer');
goog.provide('cz.payola.web.client.graph.Quadrant');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.Vector');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('scala.math');
cz.payola.web.client.graph.Drawer = function(layerEdges, layerVertices, layerText) {
var self = this;
self.layerEdges = layerEdges;
self.layerVertices = layerVertices;
self.layerText = layerText;
};
cz.payola.web.client.graph.Drawer.prototype.drawEdge = function(vertexA, vertexB, colorToUse) {
var self = this;
self.layerEdges.context.strokeStyle = colorToUse.toString();
self.layerEdges.context.lineWidth = cz.payola.web.client.graph.Constants.EdgeWidth;
self.layerEdges.context.beginPath();
var A = vertexA.position;
var B = vertexB.position;
var ctrl1 = cz.payola.web.client.Point.Zero;
var ctrl2 = cz.payola.web.client.Point.Zero;
var diff = new cz.payola.web.client.Point(scala.math.abs((A.x - B.x)), scala.math.abs((A.y - B.y)));
var quadrant = (function() {
if ((A.x <= B.x)) {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.graph.Quadrant.RightBottom;
} else {
return cz.payola.web.client.graph.Quadrant.RightTop;
}})();
} else {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.graph.Quadrant.LeftBottom;
} else {
return cz.payola.web.client.graph.Quadrant.LeftTop;
}})();
}})();
(function() {
if ((diff.x >= diff.y)) {
(function($selector_1) {
if (($selector_1 === cz.payola.web.client.graph.Quadrant.RightBottom) || ($selector_1 === cz.payola.web.client.graph.Quadrant.RightTop)) {
ctrl1.x = (A.x + (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x - (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
if (($selector_1 === cz.payola.web.client.graph.Quadrant.LeftBottom) || ($selector_1 === cz.payola.web.client.graph.Quadrant.LeftTop)) {
ctrl1.x = (A.x - (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x + (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
})(quadrant);
} else {
(function($selector_2) {
if (($selector_2 === cz.payola.web.client.graph.Quadrant.RightBottom) || ($selector_2 === cz.payola.web.client.graph.Quadrant.RightTop)) {
ctrl1.x = A.x;
ctrl1.y = (A.y + (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y - (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
return;
}
if (($selector_2 === cz.payola.web.client.graph.Quadrant.LeftBottom) || ($selector_2 === cz.payola.web.client.graph.Quadrant.LeftTop)) {
ctrl1.x = A.x;
ctrl1.y = (A.y - (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y + (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
return;
}
})(quadrant);
}})();
self.layerEdges.context.moveTo(A.x, A.y);
self.layerEdges.context.bezierCurveTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, B.x, B.y);
self.layerEdges.context.stroke();
};
cz.payola.web.client.graph.Drawer.prototype.drawVertex = function(vertex, text, colorToUse) {
var self = this;
self.drawRoundedRectangle(self.layerVertices.context, vertex.position.add(new cz.payola.web.client.Vector((cz.payola.web.client.graph.Constants.VertexWidth / 2), (cz.payola.web.client.graph.Constants.VertexHeight / 2))), cz.payola.web.client.graph.Constants.VertexWidth, cz.payola.web.client.graph.Constants.VertexHeight, cz.payola.web.client.graph.Constants.VertexCornerRadius);
self.layerVertices.context.fillStyle = colorToUse.toString();
self.layerVertices.context.fill();
self.drawText(text, vertex.position, '18px Sans', 'center');
};
cz.payola.web.client.graph.Drawer.prototype.drawText = function(text, position, textAlign, textFont) {
var self = this;
self.layerText.context.fillStyle = cz.payola.web.client.graph.Constants.ColorText.toString();
self.layerText.context.font = textFont;
self.layerText.context.textAlign = textAlign;
self.layerText.context.fillText(text, (position.x + cz.payola.web.client.graph.Constants.TextCoordCorrectionX), (position.y + cz.payola.web.client.graph.Constants.TextCoordCorrectionY));
};
cz.payola.web.client.graph.Drawer.prototype.drawRoundedRectangle = function(context, coord, width, height, radius) {
var self = this;
context.beginPath();
var aX = (coord.x + radius);
var aY = coord.y;
self.layerEdges.context.moveTo(aX, aY);
aX = coord.x;
aY = coord.y;
context.quadraticCurveTo(aX, aY, aX, (aY + radius));
aX = coord.x;
aY = (coord.y + height);
context.lineTo(aX, (aY - radius));
context.quadraticCurveTo(aX, aY, (aX + radius), aY);
aX = (coord.x + width);
aY = (coord.y + height);
context.lineTo((aX - radius), aY);
context.quadraticCurveTo(aX, aY, aX, (aY - radius));
aX = (coord.x + width);
aY = coord.y;
context.lineTo(aX, (aY + radius));
context.quadraticCurveTo(aX, aY, (aX - radius), aY);
context.closePath();
};
cz.payola.web.client.graph.Drawer.prototype.drawSelectionByRect = function(origin, direction, colorToUse) {
var self = this;
self.layerEdges.context.strokeStyle = colorToUse.toString();
self.layerEdges.context.lineWidth = cz.payola.web.client.graph.Constants.SelectLineWidth;
self.layerEdges.context.rect(scala.math.min(origin.x, direction.x), scala.math.min(origin.y, direction.y), scala.math.abs((origin.x - direction.x)), scala.math.abs((origin.y - direction.y)));
self.layerEdges.context.stroke();
};
cz.payola.web.client.graph.Drawer.prototype.drawGraph = function(graph) {
var self = this;
graph.getGraph().foreach(function(vertex) { vertex.neighbours.foreach(function(neighbourVertex) { (function() {
if ((vertex.id < neighbourVertex.id)) {
var edgeColor = (function() {
if ((vertex.selected || neighbourVertex.selected)) {
return cz.payola.web.client.graph.Constants.ColorEdgeSelect;
} else {
return cz.payola.web.client.graph.Constants.ColorEdge;
}})();
self.drawEdge(vertex, neighbourVertex, edgeColor);
} else {
}})();
 });
 });
var somethingSelected = graph.getGraph().exists(function($x$1) { return $x$1.selected;
 });
graph.getGraph().foreach(function(vertex) { var neighbourSelected = vertex.neighbours.exists(function($x$2) { return $x$2.selected;
 });
(function() {
if (vertex.selected) {
self.drawVertex(vertex, vertex.text, cz.payola.web.client.graph.Constants.ColorVertexHigh);
} else {
(function() {
if (neighbourSelected) {
self.drawVertex(vertex, '', cz.payola.web.client.graph.Constants.ColorVertexMedium);
} else {
(function() {
if ((! somethingSelected)) {
self.drawVertex(vertex, vertex.text, cz.payola.web.client.graph.Constants.ColorVertexDefault);
} else {
self.drawVertex(vertex, '', cz.payola.web.client.graph.Constants.ColorVertexLow);
}})();
}})();
}})();
 });
};
cz.payola.web.client.graph.Drawer.prototype.clear = function(context, x, y, width, height) {
var self = this;
context.fillStyle = cz.payola.web.client.graph.Constants.ColorBackground.toString();
context.fillRect(x, y, width, height);
};
cz.payola.web.client.graph.Drawer.prototype.redraw = function(graph) {
var self = this;
self.clear(self.layerEdges.context, 0.0, 0.0, self.layerEdges.getWidth(), self.layerEdges.getHeight());
self.clear(self.layerVertices.context, 0.0, 0.0, self.layerVertices.getWidth(), self.layerVertices.getHeight());
self.clear(self.layerText.context, 0.0, 0.0, self.layerText.getWidth(), self.layerText.getWidth());
self.drawGraph(graph);
};
cz.payola.web.client.graph.Drawer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Drawer', []);
cz.payola.web.client.graph.Quadrant.RightBottom = 1;
cz.payola.web.client.graph.Quadrant.LeftBottom = 2;
cz.payola.web.client.graph.Quadrant.LeftTop = 3;
cz.payola.web.client.graph.Quadrant.RightTop = 4;
cz.payola.web.client.graph.Quadrant.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Quadrant', []);
