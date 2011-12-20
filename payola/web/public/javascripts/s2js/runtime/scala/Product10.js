goog.provide('scala.Product10');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product10 = function() {
var self = this;
goog.object.extend(self, new scala.Product());
};
scala.Product10.prototype.productArity = function() {
var self = this;
return 10;
};
scala.Product10.prototype.productElement = function(n) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return self._1();
}
if ($selector_1 === 1) {
return self._2();
}
if ($selector_1 === 2) {
return self._3();
}
if ($selector_1 === 3) {
return self._4();
}
if ($selector_1 === 4) {
return self._5();
}
if ($selector_1 === 5) {
return self._6();
}
if ($selector_1 === 6) {
return self._7();
}
if ($selector_1 === 7) {
return self._8();
}
if ($selector_1 === 8) {
return self._9();
}
if ($selector_1 === 9) {
return self._10();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
};
scala.Product10.prototype.metaClass_ = new s2js.MetaClass('scala.Product10', [scala.Product]);
scala.Product10.unapply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Product10.metaClass_ = new s2js.MetaClass('scala.Product10', []);
