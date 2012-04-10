goog.provide('scala.Tuple9');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product9');
goog.require('scala.Some');
scala.Tuple9 = function(_1, _2, _3, _4, _5, _6, _7, _8, _9) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
self._4 = _4;
self._5 = _5;
self._6 = _6;
self._7 = _7;
self._8 = _8;
self._9 = _9;
goog.base(self);
};
goog.inherits(scala.Tuple9, scala.Product9);
goog.object.extend(scala.Tuple9.prototype, new scala.Product());
scala.Tuple9.prototype.toString = function() {
var self = this;
return ((((((((((((((((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ',') + self._4) + ',') + self._5) + ',') + self._6) + ',') + self._7) + ',') + '') + self._8) + ',') + self._9) + ')');
};
scala.Tuple9.prototype.copy = function(_1, _2, _3, _4, _5, _6, _7, _8, _9) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
if (typeof(_4) === 'undefined') { _4 = self._4; }
if (typeof(_5) === 'undefined') { _5 = self._5; }
if (typeof(_6) === 'undefined') { _6 = self._6; }
if (typeof(_7) === 'undefined') { _7 = self._7; }
if (typeof(_8) === 'undefined') { _8 = self._8; }
if (typeof(_9) === 'undefined') { _9 = self._9; }
return new scala.Tuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9);
};
scala.Tuple9.prototype.productPrefix = function() {
var self = this;
return 'Tuple9';
};
scala.Tuple9.prototype.__class__ = new s2js.Class('scala.Tuple9', [scala.Product9, scala.Product]);
scala.Tuple9.toString = function() {
var self = this;
return 'Tuple9';
};
scala.Tuple9.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple9(x$0._1, x$0._2, x$0._3, x$0._4, x$0._5, x$0._6, x$0._7, x$0._8, x$0._9));
}
})();
};
scala.Tuple9.$apply = function(_1, _2, _3, _4, _5, _6, _7, _8, _9) {
var self = this;
return new scala.Tuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9);
};
scala.Tuple9.__class__ = new s2js.Class('scala.Tuple9', []);
