goog.provide('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.Vector');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Some');
goog.require('scala.Tuple2');
goog.require('scala.runtime.ScalaRunTime');
cz.payola.web.client.views.Point = function(x, y) {
var self = this;
self.x = x;
self.y = y;
goog.base(self);};
goog.inherits(cz.payola.web.client.views.Point, scala.Product);
cz.payola.web.client.views.Point.prototype.$plus = function(v) {
var self = this;
return new cz.payola.web.client.views.Point((self.x + v.x), (self.y + v.y));
};
cz.payola.web.client.views.Point.prototype.$minus = function(p) {
var self = this;
return new cz.payola.web.client.views.Vector((self.x - p.x), (self.y - p.y));
};
cz.payola.web.client.views.Point.prototype.$less$eq = function(p) {
var self = this;
return ((self.x <= p.x) && (self.y <= p.y));
};
cz.payola.web.client.views.Point.prototype.$greater$eq = function(p) {
var self = this;
return ((self.x >= p.x) && (self.y >= p.y));
};
cz.payola.web.client.views.Point.prototype.toVector = function() {
var self = this;
return new cz.payola.web.client.views.Vector(self.x, self.y);
};
cz.payola.web.client.views.Point.prototype.copy = function(x, y) {
var self = this;
if (typeof(x) === 'undefined') { x = self.x; }
if (typeof(y) === 'undefined') { y = self.y; }
return new cz.payola.web.client.views.Point(x, y);
};
cz.payola.web.client.views.Point.prototype.toString = function() {
var self = this;
return scala.runtime.ScalaRunTime._toString(self);
};
cz.payola.web.client.views.Point.prototype.productPrefix = function() {
var self = this;
return 'Point';
};
cz.payola.web.client.views.Point.prototype.productArity = function() {
var self = this;
return 2;
};
cz.payola.web.client.views.Point.prototype.productElement = function($x$1) {
var self = this;
return (function($selector$1) {
if ($selector$1 === 0) {
return cz.payola.web.client.views.Point.x;
}
if ($selector$1 === 1) {
return cz.payola.web.client.views.Point.y;
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
cz.payola.web.client.views.Point.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.Point', [scala.Product]);
cz.payola.web.client.views.Point.Zero = new cz.payola.web.client.views.Point(0.0, 0.0);
cz.payola.web.client.views.Point.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple2(x$0.x, x$0.y));
}})();
};
cz.payola.web.client.views.Point.$apply = function(x, y) {
var self = this;
return new cz.payola.web.client.views.Point(x, y);
};
cz.payola.web.client.views.Point.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.Point', []);
