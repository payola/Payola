goog.provide('scala.Product3');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product3 = function() {
var self = this;
goog.object.extend(self, new scala.Product());
};
scala.Product3.prototype.productArity = function() {
var self = this;
return 3;
};
scala.Product3.prototype.productElement = function(n) {
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
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
};
scala.Product3.prototype.metaClass_ = new s2js.MetaClass('scala.Product3', [scala.Product]);
scala.Product3.unapply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Product3.metaClass_ = new s2js.MetaClass('scala.Product3', []);
