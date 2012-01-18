goog.provide('cz.payola.web.client.views.graph.EdgeView');
goog.provide('cz.payola.web.client.views.graph.Quadrant');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Point');
goog.require('scala.math');
cz.payola.web.client.views.graph.EdgeView = function(edgeModel, originView, destinationView) {
var self = this;
self.edgeModel = edgeModel;
self.originView = originView;
self.destinationView = destinationView;
};
cz.payola.web.client.views.graph.EdgeView.prototype.draw = function(context) {
var self = this;
var A = self.originView.position;
var B = self.destinationView.position;
var ctrl1 = new cz.payola.web.client.views.Point(0.0, 0.0);
var ctrl2 = new cz.payola.web.client.views.Point(0.0, 0.0);
var diff = new cz.payola.web.client.views.Point(scala.math.abs((A.x - B.x)), scala.math.abs((A.y - B.y)));
var quadrant = (function() {
if ((A.x <= B.x)) {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.views.graph.Quadrant.RightBottom;
} else {
return cz.payola.web.client.views.graph.Quadrant.RightTop;
}})();
} else {
return (function() {
if ((A.y <= B.y)) {
return cz.payola.web.client.views.graph.Quadrant.LeftBottom;
} else {
return cz.payola.web.client.views.graph.Quadrant.LeftTop;
}})();
}})();
(function() {
if ((diff.x >= diff.y)) {
(function($selector_1) {
if (($selector_1 === cz.payola.web.client.views.graph.Quadrant.RightBottom) || ($selector_1 === cz.payola.web.client.views.graph.Quadrant.RightTop)) {
ctrl1.x = (A.x + (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x - (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
if (($selector_1 === cz.payola.web.client.views.graph.Quadrant.LeftBottom) || ($selector_1 === cz.payola.web.client.views.graph.Quadrant.LeftTop)) {
ctrl1.x = (A.x - (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x + (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.y = B.y;
return;
}
})(quadrant);
} else {
(function($selector_2) {
if (($selector_2 === cz.payola.web.client.views.graph.Quadrant.RightBottom) || ($selector_2 === cz.payola.web.client.views.graph.Quadrant.LeftBottom)) {
ctrl1.x = A.x;
ctrl1.y = (A.y + (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y - (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
return;
}
if (($selector_2 === cz.payola.web.client.views.graph.Quadrant.RightTop) || ($selector_2 === cz.payola.web.client.views.graph.Quadrant.LeftTop)) {
ctrl1.x = A.x;
ctrl1.y = (A.y - (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y + (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
return;
}
})(quadrant);
}})();
(function() {
if ((self.originView.selected || self.destinationView.selected)) {
context.strokeStyle = cz.payola.web.client.views.Constants.ColorEdgeSelect.toString();
} else {
context.strokeStyle = cz.payola.web.client.views.Constants.ColorEdge.toString();
}})();
context.lineWidth = cz.payola.web.client.views.Constants.EdgeWidth;
context.beginPath();
context.moveTo(A.x, A.y);
context.bezierCurveTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, B.x, B.y);
context.stroke();
};
cz.payola.web.client.views.graph.EdgeView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.EdgeView', []);
cz.payola.web.client.views.graph.Quadrant.RightBottom = 1;
cz.payola.web.client.views.graph.Quadrant.LeftBottom = 2;
cz.payola.web.client.views.graph.Quadrant.LeftTop = 3;
cz.payola.web.client.views.graph.Quadrant.RightTop = 4;
cz.payola.web.client.views.graph.Quadrant.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.Quadrant', []);
