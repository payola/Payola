goog.provide('scala.Tuple7');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product7');
goog.require('scala.Some');
scala.Tuple7 = function(_1, _2, _3, _4, _5, _6, _7) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
self._4 = _4;
self._5 = _5;
self._6 = _6;
self._7 = _7;
goog.object.extend(self, new scala.Product());
goog.object.extend(self, new scala.Product7());
};
scala.Tuple7.prototype.toString = function() {
var self = this;
return (((((((((((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ',') + self._4) + ',') + self._5) + ',') + self._6) + ',') + self._7) + ')');
};
scala.Tuple7.prototype.copy = function(_1, _2, _3, _4, _5, _6, _7) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
if (typeof(_4) === 'undefined') { _4 = self._4; }
if (typeof(_5) === 'undefined') { _5 = self._5; }
if (typeof(_6) === 'undefined') { _6 = self._6; }
if (typeof(_7) === 'undefined') { _7 = self._7; }
return new scala.Tuple7(_1, _2, _3, _4, _5, _6, _7);
};
scala.Tuple7.prototype.productPrefix = function() {
var self = this;
return 'Tuple7';
};
scala.Tuple7.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple7', [scala.Product7, scala.Product]);
scala.Tuple7.toString = function() {
var self = this;
return 'Tuple7';
};
scala.Tuple7.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple7(x$0._1, x$0._2, x$0._3, x$0._4, x$0._5, x$0._6, x$0._7));
}})();
};
scala.Tuple7.$apply = function(_1, _2, _3, _4, _5, _6, _7) {
var self = this;
return new scala.Tuple7(_1, _2, _3, _4, _5, _6, _7);
};
scala.Tuple7.metaClass_ = new s2js.MetaClass('scala.Tuple7', []);
