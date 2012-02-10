goog.provide('scala.Tuple4');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product4');
goog.require('scala.Some');
scala.Tuple4 = function(_1, _2, _3, _4) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
self._4 = _4;
self._1 = undefined;
self._2 = undefined;
self._3 = undefined;
self._4 = undefined;
goog.base(self);goog.object.extend(self, new scala.Product());
};
goog.inherits(scala.Tuple4, scala.Product4);
scala.Tuple4.prototype.toString = function() {
var self = this;
return (((((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ',') + self._4) + ')');
};
scala.Tuple4.prototype.copy = function(_1, _2, _3, _4) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
if (typeof(_4) === 'undefined') { _4 = self._4; }
return new scala.Tuple4(_1, _2, _3, _4);
};
scala.Tuple4.prototype.productPrefix = function() {
var self = this;
return 'Tuple4';
};
scala.Tuple4.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple4', [scala.Product4, scala.Product]);
scala.Tuple4.toString = function() {
var self = this;
return 'Tuple4';
};
scala.Tuple4.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple4(x$0._1, x$0._2, x$0._3, x$0._4));
}})();
};
scala.Tuple4.$apply = function(_1, _2, _3, _4) {
var self = this;
return new scala.Tuple4(_1, _2, _3, _4);
};
scala.Tuple4.metaClass_ = new s2js.MetaClass('scala.Tuple4', []);
