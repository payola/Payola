goog.provide('scala.Product2');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product2 = function() {
var self = this;
};
goog.inherits(scala.Product2, scala.Product);
scala.Product2.prototype.productArity = function() {
var self = this;
return 2;
}
;
scala.Product2.prototype.productElement = function(n) {
var self = this;
return (function($selector$1) {
if ($selector$1 === 0) {
return self._1();
}
if ($selector$1 === 1) {
return self._2();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
}
;
scala.Product2.prototype.metaClass_ = new s2js.MetaClass('scala.Product2', [scala.Product]);
scala.Product2.unapply = function(x) {
var self = this;
return new scala.Some(x);
}
;
scala.Product2.metaClass_ = new s2js.MetaClass('scala.Product2', []);
