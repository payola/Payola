goog.provide('cz.payola.web.client.views.graph.VertexView');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.graph.InformationView');
goog.require('cz.payola.web.client.views.graph.View');
cz.payola.web.client.views.graph.VertexView = function(vertexModel, position) {
var self = this;
self.vertexModel = vertexModel;
self.position = position;
self.selected = false;
self.information = new cz.payola.web.client.views.graph.InformationView(self.vertexModel);
goog.base(self);};
goog.inherits(cz.payola.web.client.views.graph.VertexView, cz.payola.web.client.views.graph.View);
cz.payola.web.client.views.graph.VertexView.prototype.draw = function(context, color, positionCorrection) {
var self = this;
var correction = (function() {
if ((positionCorrection != null)) {
return positionCorrection.toVector();
} else {
return cz.payola.web.client.views.Point.Zero.toVector();
}})();
self.drawRoundedRectangle(context, self.position.$plus(cz.payola.web.client.views.Constants.VertexSize.$div(-2.0)).$plus(correction), cz.payola.web.client.views.Constants.VertexSize, cz.payola.web.client.views.Constants.VertexCornerRadius);
context.fillStyle = (function() {
if ((color != null)) {
return color.toString();
} else {
return cz.payola.web.client.views.Constants.ColorVertexDefault.toString();
}})();
context.fill();
};
cz.payola.web.client.views.graph.VertexView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.VertexView', [cz.payola.web.client.views.graph.View]);
