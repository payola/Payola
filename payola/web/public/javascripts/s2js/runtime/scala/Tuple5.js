goog.provide('scala.Tuple5');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product5');
goog.require('scala.Some');
scala.Tuple5 = function(_1, _2, _3, _4, _5) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
self._4 = _4;
self._5 = _5;
goog.object.extend(self, new scala.Product());
goog.object.extend(self, new scala.Product5());
};
scala.Tuple5.prototype.toString = function() {
var self = this;
return (((((((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ',') + self._4) + ',') + self._5) + ')');
};
scala.Tuple5.prototype.copy = function(_1, _2, _3, _4, _5) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
if (typeof(_4) === 'undefined') { _4 = self._4; }
if (typeof(_5) === 'undefined') { _5 = self._5; }
return new scala.Tuple5(_1, _2, _3, _4, _5);
};
scala.Tuple5.prototype.productPrefix = function() {
var self = this;
return 'Tuple5';
};
scala.Tuple5.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple5', [scala.Product5, scala.Product]);
scala.Tuple5.toString = function() {
var self = this;
return 'Tuple5';
};
scala.Tuple5.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple5(x$0._1, x$0._2, x$0._3, x$0._4, x$0._5));
}})();
};
scala.Tuple5.$apply = function(_1, _2, _3, _4, _5) {
var self = this;
return new scala.Tuple5(_1, _2, _3, _4, _5);
};
scala.Tuple5.metaClass_ = new s2js.MetaClass('scala.Tuple5', []);
