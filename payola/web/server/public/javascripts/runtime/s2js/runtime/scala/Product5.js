goog.provide('scala.Product5');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product5 = function() {
var self = this;
};
goog.inherits(scala.Product5, scala.Product);
scala.Product5.prototype.productArity = function() {
var self = this;
return 5;
};
scala.Product5.prototype.productElement = function(n) {
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
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
};
scala.Product5.prototype.__class__ = new s2js.Class('scala.Product5', [scala.Product]);
scala.Product5.unapply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Product5.__class__ = new s2js.Class('scala.Product5', []);
