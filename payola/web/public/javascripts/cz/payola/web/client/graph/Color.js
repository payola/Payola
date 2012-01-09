goog.provide('cz.payola.web.client.graph.Color');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Some');
goog.require('scala.Tuple4');
cz.payola.web.client.graph.Color = function(red, green, blue, alpha) {
var self = this;
if (typeof(alpha) === 'undefined') { alpha = 1.0; }
self.red = red;
self.green = green;
self.blue = blue;
self.alpha = alpha;
goog.base(self);};
goog.inherits(cz.payola.web.client.graph.Color, scala.Product);
cz.payola.web.client.graph.Color.prototype.toString = function() {
var self = this;
return (((((((('rgba(' + self.red) + ', ') + self.green) + ', ') + self.blue) + ', ') + self.alpha) + ')');
};
cz.payola.web.client.graph.Color.prototype.copy = function(red, green, blue, alpha) {
var self = this;
if (typeof(red) === 'undefined') { red = self.red; }
if (typeof(green) === 'undefined') { green = self.green; }
if (typeof(blue) === 'undefined') { blue = self.blue; }
if (typeof(alpha) === 'undefined') { alpha = self.alpha; }
return new cz.payola.web.client.graph.Color(red, green, blue, alpha);
};
cz.payola.web.client.graph.Color.prototype.productPrefix = function() {
var self = this;
return 'Color';
};
cz.payola.web.client.graph.Color.prototype.productArity = function() {
var self = this;
return 4;
};
cz.payola.web.client.graph.Color.prototype.productElement = function($x$1) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return cz.payola.web.client.graph.Color.red;
}
if ($selector_1 === 1) {
return cz.payola.web.client.graph.Color.green;
}
if ($selector_1 === 2) {
return cz.payola.web.client.graph.Color.blue;
}
if ($selector_1 === 3) {
return cz.payola.web.client.graph.Color.alpha;
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
cz.payola.web.client.graph.Color.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Color', [scala.Product]);
cz.payola.web.client.graph.Color.toString = function() {
var self = this;
return 'Color';
};
cz.payola.web.client.graph.Color.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple4(x$0.red, x$0.green, x$0.blue, x$0.alpha));
}})();
};
cz.payola.web.client.graph.Color.$apply = function(red, green, blue, alpha) {
var self = this;
if (typeof(alpha) === 'undefined') { alpha = 1.0; }
return new cz.payola.web.client.graph.Color(red, green, blue, alpha);
};
cz.payola.web.client.graph.Color.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Color', []);
