goog.provide('scala.Product1');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.Product');
goog.require('scala.Some');
scala.Product1 = function() {
var self = this;
goog.object.extend(self, new scala.Product());
};
scala.Product1.prototype.productArity = function() {
var self = this;
return 1;
};
scala.Product1.prototype.productElement = function(n) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return self._1();
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException(n.toString());
})();
}
})(n);
};
scala.Product1.prototype.metaClass_ = new s2js.MetaClass('scala.Product1', [scala.Product]);
scala.Product1.unapply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Product1.metaClass_ = new s2js.MetaClass('scala.Product1', []);
