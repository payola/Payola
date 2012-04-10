goog.provide('scala.Tuple8');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product8');
goog.require('scala.Some');
scala.Tuple8 = function(_1, _2, _3, _4, _5, _6, _7, _8) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
self._4 = _4;
self._5 = _5;
self._6 = _6;
self._7 = _7;
self._8 = _8;
goog.base(self);
};
goog.inherits(scala.Tuple8, scala.Product8);
goog.object.extend(scala.Tuple8.prototype, new scala.Product());
scala.Tuple8.prototype.toString = function() {
var self = this;
return ((((((((((((((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ',') + self._4) + ',') + self._5) + ',') + self._6) + ',') + self._7) + ',') + '') + self._8) + ')');
};
scala.Tuple8.prototype.copy = function(_1, _2, _3, _4, _5, _6, _7, _8) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
if (typeof(_4) === 'undefined') { _4 = self._4; }
if (typeof(_5) === 'undefined') { _5 = self._5; }
if (typeof(_6) === 'undefined') { _6 = self._6; }
if (typeof(_7) === 'undefined') { _7 = self._7; }
if (typeof(_8) === 'undefined') { _8 = self._8; }
return new scala.Tuple8(_1, _2, _3, _4, _5, _6, _7, _8);
};
scala.Tuple8.prototype.productPrefix = function() {
var self = this;
return 'Tuple8';
};
scala.Tuple8.prototype.__class__ = new s2js.Class('scala.Tuple8', [scala.Product8, scala.Product]);
scala.Tuple8.toString = function() {
var self = this;
return 'Tuple8';
};
scala.Tuple8.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple8(x$0._1, x$0._2, x$0._3, x$0._4, x$0._5, x$0._6, x$0._7, x$0._8));
}
})();
};
scala.Tuple8.$apply = function(_1, _2, _3, _4, _5, _6, _7, _8) {
var self = this;
return new scala.Tuple8(_1, _2, _3, _4, _5, _6, _7, _8);
};
scala.Tuple8.__class__ = new s2js.Class('scala.Tuple8', []);
