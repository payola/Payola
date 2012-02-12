goog.provide('cz.payola.web.client.views.graph.EdgeView');
goog.provide('cz.payola.web.client.views.graph.Quadrant');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.graph.InformationView');
goog.require('cz.payola.web.client.views.graph.View');
goog.require('scala.math');
cz.payola.web.client.views.graph.EdgeView = function(edgeModel, originView, destinationView) {
var self = this;
self.edgeModel = edgeModel;
self.originView = originView;
self.destinationView = destinationView;
self.information = new cz.payola.web.client.views.graph.InformationView(self.edgeModel);
goog.base(self);};
goog.inherits(cz.payola.web.client.views.graph.EdgeView, cz.payola.web.client.views.graph.View);
cz.payola.web.client.views.graph.EdgeView.prototype.isSelected = function() {
var self = this;
return (self.originView.selected || self.destinationView.selected);
};
cz.payola.web.client.views.graph.EdgeView.prototype.draw = function(context, color, positionCorrection) {
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
if ((diff.x >= diff.y)) {
(function($selector$1) {
if (($selector$1 === cz.payola.web.client.views.graph.Quadrant.RightBottom) || ($selector$1 === cz.payola.web.client.views.graph.Quadrant.RightTop)) {
ctrl1.x = (A.x + (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x - (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.y = B.y;

return;
}
if (($selector$1 === cz.payola.web.client.views.graph.Quadrant.LeftBottom) || ($selector$1 === cz.payola.web.client.views.graph.Quadrant.LeftTop)) {
ctrl1.x = (A.x - (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl1.y = A.y;
ctrl2.x = (B.x + (diff.x / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.y = B.y;

return;
}
})(quadrant);
} else {
(function($selector$2) {
if (($selector$2 === cz.payola.web.client.views.graph.Quadrant.RightBottom) || ($selector$2 === cz.payola.web.client.views.graph.Quadrant.LeftBottom)) {
ctrl1.x = A.x;
ctrl1.y = (A.y + (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y - (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));

return;
}
if (($selector$2 === cz.payola.web.client.views.graph.Quadrant.RightTop) || ($selector$2 === cz.payola.web.client.views.graph.Quadrant.LeftTop)) {
ctrl1.x = A.x;
ctrl1.y = (A.y - (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));
ctrl2.x = B.x;
ctrl2.y = (B.y + (diff.y / cz.payola.web.client.views.Constants.EdgeSIndex));

return;
}
})(quadrant);
}
var colorToUse = (function() {
if ((color != null)) {
return color;
} else {
return (function() {
if (self.isSelected()) {
return cz.payola.web.client.views.Constants.ColorEdgeSelect;
} else {
return cz.payola.web.client.views.Constants.ColorEdge;
}})();
}})();
var correction = (function() {
if ((positionCorrection != null)) {
return positionCorrection.toVector();
} else {
return new cz.payola.web.client.views.Point(0.0, 0.0).toVector();
}})();
self.drawBezierCurve(context, ctrl1.$plus(correction), ctrl2.$plus(correction), A.$plus(correction), B.$plus(correction), cz.payola.web.client.views.Constants.EdgeWidth, colorToUse);

};
cz.payola.web.client.views.graph.EdgeView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.EdgeView', [cz.payola.web.client.views.graph.View]);
cz.payola.web.client.views.graph.Quadrant.RightBottom = 1;
cz.payola.web.client.views.graph.Quadrant.LeftBottom = 2;
cz.payola.web.client.views.graph.Quadrant.LeftTop = 3;
cz.payola.web.client.views.graph.Quadrant.RightTop = 4;
cz.payola.web.client.views.graph.Quadrant.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.Quadrant', []);
