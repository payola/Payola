goog.provide('scala.Product1');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product1 = function() {
var self = this;
};
goog.inherits(scala.Product1, scala.Product);
scala.Product1.prototype.productArity = function() {
var self = this;
return 1;
};
scala.Product1.prototype.productElement = function(n) {
var self = this;
return (function($selector$1) {
if ($selector$1 === 0) {
return self._1();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
};
scala.Product1.prototype.__class__ = new s2js.Class('scala.Product1', [scala.Product]);
scala.Product1.unapply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Product1.__class__ = new s2js.Class('scala.Product1', []);
