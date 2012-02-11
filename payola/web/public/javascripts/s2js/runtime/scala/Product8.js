goog.provide('scala.Product8');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product8 = function() {
var self = this;
};
goog.inherits(scala.Product8, scala.Product);
scala.Product8.prototype.productArity = function() {
var self = this;
return 8;
}
;
scala.Product8.prototype.productElement = function(n) {
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
if ($selector$1 === 6) {
return self._7();
}
if ($selector$1 === 7) {
return self._8();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
}
;
scala.Product8.prototype.metaClass_ = new s2js.MetaClass('scala.Product8', [scala.Product]);
scala.Product8.unapply = function(x) {
var self = this;
return new scala.Some(x);
}
;
scala.Product8.metaClass_ = new s2js.MetaClass('scala.Product8', []);
