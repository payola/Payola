goog.provide('scala.Tuple1');
goog.require('scala.None');
goog.require('scala.Product');
goog.require('scala.Product1');
goog.require('scala.Some');
scala.Tuple1 = function(_1) {
var self = this;
self._1 = _1;
goog.base(self);goog.object.extend(self, new scala.Product());
};
goog.inherits(scala.Tuple1, scala.Product1);
scala.Tuple1.prototype.toString = function() {
var self = this;
return (('(' + self._1) + ')');
};
scala.Tuple1.prototype.copy = function(_1) {
var self = this;
if (typeof(_1) === 'undefined') { _1 = self._1; }
return new scala.Tuple1(_1);
};
scala.Tuple1.prototype.productPrefix = function() {
var self = this;
return 'Tuple1';
};
scala.Tuple1.prototype.metaClass_ = new s2js.MetaClass('scala.Tuple1', [scala.Product1, scala.Product]);
scala.Tuple1.toString = function() {
var self = this;
return 'Tuple1';
};
scala.Tuple1.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(x$0._1);
}})();
};
scala.Tuple1.$apply = function(_1) {
var self = this;
return new scala.Tuple1(_1);
};
scala.Tuple1.metaClass_ = new s2js.MetaClass('scala.Tuple1', []);
