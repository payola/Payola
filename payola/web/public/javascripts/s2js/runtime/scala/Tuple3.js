goog.provide('scala.Tuple3');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product3');
goog.require('scala.Some');
scala.Tuple3 = function(_1, _2, _3) {
var self = this;
self._1 = _1;
self._2 = _2;
self._3 = _3;
goog.base(self);goog.object.extend(self, new scala.Product());
};
goog.inherits(scala.Tuple3, scala.Product3);
scala.Tuple3.prototype.toString = function() {
var self = this;
return (((((('(' + self._1) + ',') + self._2) + ',') + self._3) + ')');
};
scala.Tuple3.prototype.copy = function(_1, _2, _3) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
if (typeof(_3) === 'undefined') { _3 = self._3; }
return new scala.Tuple3(_1, _2, _3);
};
scala.Tuple3.prototype.productPrefix = function() {
var self = this;
return 'Tuple3';
};
scala.Tuple3.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple3', [scala.Product3, scala.Product]);
scala.Tuple3.toString = function() {
var self = this;
return 'Tuple3';
};
scala.Tuple3.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple3(x$0._1, x$0._2, x$0._3));
}})();
};
scala.Tuple3.$apply = function(_1, _2, _3) {
var self = this;
return new scala.Tuple3(_1, _2, _3);
};
scala.Tuple3.metaClass_ = new s2js.MetaClass('scala.Tuple3', []);
