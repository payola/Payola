goog.provide('scala.Product6');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product6 = function() {
var self = this;
};
goog.inherits(scala.Product6, scala.Product);
scala.Product6.prototype.productArity = function() {
var self = this;
return 6;
}
;
scala.Product6.prototype.productElement = function(n) {
var self = this;
return (function($selector$1) {
if ($selector$1 === 0) {
return self._1();
}
if ($selector$1 === 1) {
return self._2();
}
if ($selector$1 === 2) {
return self._3();
}
if ($selector$1 === 3) {
return self._4();
}
if ($selector$1 === 4) {
return self._5();
}
if ($selector$1 === 5) {
return self._6();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
}
;
scala.Product6.prototype.metaClass_ = new s2js.MetaClass('scala.Product6', [scala.Product]);
scala.Product6.unapply = function(x) {
var self = this;
return new scala.Some(x);
}
;
scala.Product6.metaClass_ = new s2js.MetaClass('scala.Product6', []);
