goog.provide('cz.payola.web.client.graph.Drawer');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('scala.math');
cz.payola.web.client.graph.Drawer = function(context) {
var self = this;
self.context = context;
};
cz.payola.web.client.graph.Drawer.prototype.drawEdge = function(vertex1, vertex2, colorToUse) {
var self = this;
self.context.strokeStyle = colorToUse.toString();
self.context.lineWidth = cz.payola.web.client.graph.Constants.EDGE_WIDTH;
self.context.beginPath();
var x1 = vertex1.x;
var y1 = vertex1.y;
var x2 = vertex2.x;
var y2 = vertex2.y;
var ctrl1X = 0.0;
var ctrl1Y = 0.0;
var ctrl2X = 0.0;
var ctrl2Y = 0.0;
var diffX = scala.math.abs((x1 - x2));
var diffY = scala.math.abs((y1 - y2));
var quadrant = (function() {
if ((x1 <= x2)) {
return (function() {
if ((y1 <= y2)) {
return 1;
} else {
return 4;
}})();
} else {
return (function() {
if ((y1 <= y2)) {
return 2;
} else {
return 3;
}})();
}})();
(function() {
if ((diffX >= diffY)) {
(function($selector_1) {
if (($selector_1 === 1) || ($selector_1 === 4)) {
ctrl1X = (x1 + (diffX / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl1Y = y1;
ctrl2X = (x2 - (diffX / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl2Y = y2;
return;
}
if (($selector_1 === 2) || ($selector_1 === 3)) {
ctrl1X = (x1 - (diffX / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl1Y = y1;
ctrl2X = (x2 + (diffX / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl2Y = y2;
return;
}
})(quadrant);
} else {
(function($selector_2) {
if (($selector_2 === 1) || ($selector_2 === 2)) {
ctrl1X = x1;
ctrl1Y = (y1 + (diffY / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl2X = x2;
ctrl2Y = (y2 - (diffY / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
return;
}
if (($selector_2 === 3) || ($selector_2 === 4)) {
ctrl1X = x1;
ctrl1Y = (y1 - (diffY / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
ctrl2X = x2;
ctrl2Y = (y2 + (diffY / cz.payola.web.client.graph.Constants.EDGE_S_INDEX));
return;
}
})(quadrant);
}})();
self.context.moveTo(x1, y1);
self.context.bezierCurveTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2);
self.context.stroke();
};
cz.payola.web.client.graph.Drawer.prototype.drawVertex = function(vertex, text, colorToUse) {
var self = this;
var x1 = (vertex.x - (cz.payola.web.client.graph.Constants.VERTEX_WIDTH / 2));
var y1 = (vertex.y - (cz.payola.web.client.graph.Constants.VERTEX_HEIGHT / 2));
self.context.beginPath();
var aX = (x1 + cz.payola.web.client.graph.Constants.VERTEX_RADIUS);
var aY = y1;
self.context.moveTo(aX, aY);
aX = x1;
aY = y1;
self.context.quadraticCurveTo(aX, aY, aX, (aY + cz.payola.web.client.graph.Constants.VERTEX_RADIUS));
aX = x1;
aY = (y1 + cz.payola.web.client.graph.Constants.VERTEX_HEIGHT);
self.context.lineTo(aX, (aY - cz.payola.web.client.graph.Constants.VERTEX_RADIUS));
self.context.quadraticCurveTo(aX, aY, (aX + cz.payola.web.client.graph.Constants.VERTEX_RADIUS), aY);
aX = (x1 + cz.payola.web.client.graph.Constants.VERTEX_WIDTH);
aY = (y1 + cz.payola.web.client.graph.Constants.VERTEX_HEIGHT);
self.context.lineTo((aX - cz.payola.web.client.graph.Constants.VERTEX_RADIUS), aY);
self.context.quadraticCurveTo(aX, aY, aX, (aY - cz.payola.web.client.graph.Constants.VERTEX_RADIUS));
aX = (x1 + cz.payola.web.client.graph.Constants.VERTEX_WIDTH);
aY = y1;
self.context.lineTo(aX, (aY + cz.payola.web.client.graph.Constants.VERTEX_RADIUS));
self.context.quadraticCurveTo(aX, aY, (aX - cz.payola.web.client.graph.Constants.VERTEX_RADIUS), aY);
self.context.closePath();
self.context.fillStyle = colorToUse.toString();
self.context.fill();
self.context.fillStyle = cz.payola.web.client.graph.Constants.COLOR_TEXT.toString();
self.context.font = '18px Sans';
self.context.textAlign = 'center';
self.context.fillText(text, (vertex.x + cz.payola.web.client.graph.Constants.TEXT_COORD_CORRECTION_X), (vertex.y + cz.payola.web.client.graph.Constants.TEXT_COORD_CORRECTION_Y));
};
cz.payola.web.client.graph.Drawer.prototype.drawSelectionByRect = function(x1, y1, x2, y2, colorToUse) {
var self = this;
self.context.strokeStyle = colorToUse.toString();
self.context.lineWidth = cz.payola.web.client.graph.Constants.SELECT_LINE_WIDTH;
self.context.beginPath();
self.context.moveTo(x1, y1);
self.context.lineTo(x1, y2);
self.context.lineTo(x2, y2);
self.context.lineTo(x2, y1);
self.context.lineTo(x1, y1);
self.context.closePath();
self.context.stroke();
};
cz.payola.web.client.graph.Drawer.prototype.drawGraph = function(graph) {
var self = this;
graph.foreach(function(vertex) { vertex.neighbours.foreach(function(neighbourVertex) { (function() {
if ((vertex.id < neighbourVertex.id)) {
var edgeColor = (function() {
if ((vertex.selected || neighbourVertex.selected)) {
return cz.payola.web.client.graph.Constants.COLOR_EDGE_SELECT;
} else {
return cz.payola.web.client.graph.Constants.COLOR_EDGE;
}})();
self.drawEdge(vertex, neighbourVertex, edgeColor);
} else {
}})();
 });
 });
var somethingSelected = graph.exists(function($x$1) { return $x$1.selected;
 });
graph.foreach(function(vertex) { var neighbourSelected = vertex.neighbours.exists(function($x$2) { return $x$2.selected;
 });
(function() {
if (vertex.selected) {
self.drawVertex(vertex, vertex.text, cz.payola.web.client.graph.Constants.COLOR_VERTEX_HIGH);
} else {
(function() {
if (neighbourSelected) {
self.drawVertex(vertex, '', cz.payola.web.client.graph.Constants.COLOR_VERTEX_MEDIUM);
} else {
(function() {
if ((! somethingSelected)) {
self.drawVertex(vertex, vertex.text, cz.payola.web.client.graph.Constants.COLOR_VERTEX);
} else {
self.drawVertex(vertex, '', cz.payola.web.client.graph.Constants.COLOR_VERTEX_LOW);
}})();
}})();
}})();
 });
};
cz.payola.web.client.graph.Drawer.prototype.clear = function(x, y, width, height) {
var self = this;
self.context.fillStyle = cz.payola.web.client.graph.Constants.COLOR_BACKGROUND.toString();
self.context.fillRect(x, y, width, height);
};
cz.payola.web.client.graph.Drawer.prototype.redraw = function(graph) {
var self = this;
self.clear(0.0, 0.0, 700.0, 700.0);
self.drawGraph(graph);
};
cz.payola.web.client.graph.Drawer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Drawer', []);
