goog.provide('cz.payola.web.client.graph.views.EdgeView');
goog.provide('cz.payola.web.client.graph.views.Quadrant');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('scala.math');
cz.payola.web.client.graph.views.EdgeView = function(vertexA, vertexB, text) {
var self = this;
self.vertexA = vertexA;
self.vertexB = vertexB;
self.text = text;
};
cz.payola.web.client.graph.views.EdgeView.prototype.draw = function(context) {
var self = this;
var A = self.vertexA.position;
var B = self.vertexB.position;
var ctrl1 = new cz.payola.web.client.Point(0.0, 0.0);
var ctrl2 = new cz.payola.web.client.Point(0.0, 0.0);
var diff = new cz.payola.web.client.Point(scala.math.abs((A.x - B.x)), scala.math.abs((A.y - B.y)));
var quadrant = (function() {
if ((A.x <= B.x)) {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.graph.views.Quadrant.RightBottom;
} else {
return cz.payola.web.client.graph.views.Quadrant.RightTop;
}})();
} else {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.graph.views.Quadrant.LeftBottom;
} else {
return cz.payola.web.client.graph.views.Quadrant.LeftTop;
}})();
}})();
(function() {
if ((diff.x >= diff.y)) {
(function($selector_1) {
if (($selector_1 === cz.payola.web.client.graph.views.Quadrant.RightBottom) || ($selector_1 === cz.payola.web.client.graph.views.Quadrant.RightTop)) {
ctrl1.x = (A.x + (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x - (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
if (($selector_1 === cz.payola.web.client.graph.views.Quadrant.LeftBottom) || ($selector_1 === cz.payola.web.client.graph.views.Quadrant.LeftTop)) {
ctrl1.x = (A.x - (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x + (diff.x / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
})(quadrant);
} else {
(function($selector_2) {
if (($selector_2 === cz.payola.web.client.graph.views.Quadrant.RightBottom) || ($selector_2 === cz.payola.web.client.graph.views.Quadrant.LeftBottom)) {
ctrl1.x = A.x;
ctrl1.y = (A.y + (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y - (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
return;
}
if (($selector_2 === cz.payola.web.client.graph.views.Quadrant.RightTop) || ($selector_2 === cz.payola.web.client.graph.views.Quadrant.LeftTop)) {
ctrl1.x = A.x;
ctrl1.y = (A.y - (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y + (diff.y / cz.payola.web.client.graph.Constants.EdgeSIndex));
return;
}
})(quadrant);
}})();
(function() {
if ((self.vertexA.selected || self.vertexB.selected)) {
context.strokeStyle = cz.payola.web.client.graph.Constants.ColorEdgeSelect.toString();
} else {
context.strokeStyle = cz.payola.web.client.graph.Constants.ColorEdge.toString();
}})();
context.lineWidth = cz.payola.web.client.graph.Constants.EdgeWidth;
context.beginPath();
context.moveTo(A.x, A.y);
context.bezierCurveTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, B.x, B.y);
context.stroke();
};
cz.payola.web.client.graph.views.EdgeView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.views.EdgeView', []);
cz.payola.web.client.graph.views.Quadrant.RightBottom = 1;
cz.payola.web.client.graph.views.Quadrant.LeftBottom = 2;
cz.payola.web.client.graph.views.Quadrant.LeftTop = 3;
cz.payola.web.client.graph.views.Quadrant.RightTop = 4;
cz.payola.web.client.graph.views.Quadrant.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.views.Quadrant', []);
