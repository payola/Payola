goog.provide('cz.payola.web.client.views.graph.InformationView');
goog.require('cz.payola.web.client.views.Constants');
goog.require('cz.payola.web.client.views.Vector');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Some');
goog.require('scala.Tuple2');
goog.require('scala.runtime.ScalaRunTime');
cz.payola.web.client.views.graph.InformationView = function(vertexModel, position) {
var self = this;
self.vertexModel = vertexModel;
self.position = position;
self.positionCorrection = new cz.payola.web.client.views.Vector(0.0, 4.0);
goog.base(self);};
goog.inherits(cz.payola.web.client.views.graph.InformationView, scala.Product);
cz.payola.web.client.views.graph.InformationView.prototype.draw = function(context) {
var self = this;
context.fillStyle = cz.payola.web.client.views.Constants.ColorText.toString();
context.font = '12px Sans';
context.textAlign = 'center';
var correctedPosition = self.position.$plus(self.positionCorrection);
context.fillText(self.vertexModel.uri, correctedPosition.x, correctedPosition.y);
};
cz.payola.web.client.views.graph.InformationView.prototype.copy = function(vertexModel, position) {
var self = this;
if (typeof(vertexModel) === 'undefined') { vertexModel = self.vertexModel; }
if (typeof(position) === 'undefined') { position = self.position; }
return new cz.payola.web.client.views.graph.InformationView(vertexModel, position);
};
cz.payola.web.client.views.graph.InformationView.prototype.toString = function() {
var self = this;
return scala.runtime.ScalaRunTime._toString(self);
};
cz.payola.web.client.views.graph.InformationView.prototype.productPrefix = function() {
var self = this;
return 'InformationView';
};
cz.payola.web.client.views.graph.InformationView.prototype.productArity = function() {
var self = this;
return 2;
};
cz.payola.web.client.views.graph.InformationView.prototype.productElement = function($x$1) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return cz.payola.web.client.views.graph.InformationView.vertexModel;
}
if ($selector_1 === 1) {
return cz.payola.web.client.views.graph.InformationView.position;
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
cz.payola.web.client.views.graph.InformationView.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.InformationView', [scala.Product]);
cz.payola.web.client.views.graph.InformationView.toString = function() {
var self = this;
return 'InformationView';
};
cz.payola.web.client.views.graph.InformationView.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple2(x$0.vertexModel, x$0.position));
}})();
};
cz.payola.web.client.views.graph.InformationView.$apply = function(vertexModel, position) {
var self = this;
return new cz.payola.web.client.views.graph.InformationView(vertexModel, position);
};
cz.payola.web.client.views.graph.InformationView.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.InformationView', []);
