goog.provide('cz.payola.web.client.graph.Information');
goog.require('cz.payola.web.client.Point');
goog.require('cz.payola.web.client.graph.Constants');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Some');
goog.require('scala.Tuple2');
goog.require('scala.runtime.ScalaRunTime');
cz.payola.web.client.graph.Information = function(text, position) {
var self = this;
self.text = text;
self.position = position;
self.positionCorrection = new cz.payola.web.client.Point(0.0, 4.0);
goog.base(self);};
goog.inherits(cz.payola.web.client.graph.Information, scala.Product);
cz.payola.web.client.graph.Information.prototype.draw = function(context) {
var self = this;
context.fillStyle = cz.payola.web.client.graph.Constants.ColorText.toString();
context.font = '12px Sans';
context.textAlign = 'center';
context.fillText(self.text, (self.position.x + self.positionCorrection.x), (self.position.y + self.positionCorrection.y));
};
cz.payola.web.client.graph.Information.prototype.copy = function(text, position) {
var self = this;
if (typeof(text) === 'undefined') { text = self.text; }
if (typeof(position) === 'undefined') { position = self.position; }
return new cz.payola.web.client.graph.Information(text, position);
};
cz.payola.web.client.graph.Information.prototype.toString = function() {
var self = this;
return scala.runtime.ScalaRunTime._toString(self);
};
cz.payola.web.client.graph.Information.prototype.productPrefix = function() {
var self = this;
return 'Information';
};
cz.payola.web.client.graph.Information.prototype.productArity = function() {
var self = this;
return 2;
};
cz.payola.web.client.graph.Information.prototype.productElement = function($x$1) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return cz.payola.web.client.graph.Information.text;
}
if ($selector_1 === 1) {
return cz.payola.web.client.graph.Information.position;
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
cz.payola.web.client.graph.Information.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Information', [scala.Product]);
cz.payola.web.client.graph.Information.toString = function() {
var self = this;
return 'Information';
};
cz.payola.web.client.graph.Information.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple2(x$0.text, x$0.position));
}})();
};
cz.payola.web.client.graph.Information.$apply = function(text, position) {
var self = this;
return new cz.payola.web.client.graph.Information(text, position);
};
cz.payola.web.client.graph.Information.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Information', []);
