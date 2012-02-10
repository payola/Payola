goog.provide('scala.Tuple2');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product2');
goog.require('scala.Some');
scala.Tuple2 = function(_1, _2) {
var self = this;
self._1 = _1;
self._2 = _2;
self._1 = undefined;
self._2 = undefined;
goog.base(self);goog.object.extend(self, new scala.Product());
};
goog.inherits(scala.Tuple2, scala.Product2);
scala.Tuple2.prototype.toString = function() {
var self = this;
return (((('(' + self._1) + ',') + self._2) + ')');
};
scala.Tuple2.prototype.copy = function(_1, _2) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
if (typeof(_2) === 'undefined') { _2 = self._2; }
return new scala.Tuple2(_1, _2);
};
scala.Tuple2.prototype.productPrefix = function() {
var self = this;
return 'Tuple2';
};
scala.Tuple2.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple2', [scala.Product2, scala.Product]);
scala.Tuple2.toString = function() {
var self = this;
return 'Tuple2';
};
scala.Tuple2.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(new scala.Tuple2(x$0._1, x$0._2));
}})();
};
scala.Tuple2.$apply = function(_1, _2) {
var self = this;
return new scala.Tuple2(_1, _2);
};
scala.Tuple2.metaClass_ = new s2js.MetaClass('scala.Tuple2', []);
