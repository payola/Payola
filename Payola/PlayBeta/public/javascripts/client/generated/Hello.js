goog.provide('generated.Hello');
goog.provide('generated.A');
goog.provide('generated.B');
generated.Hello.main = function() {
var self = this;
var b = new generated.B(4,'Ahoy');
browser.alert(b.x(3));
browser.alert(b.y('123*'));
browser.alert('Hello world');
};
/** @constructor*/
generated.A = function(foo,bar) {
var self = this;
self.foo = foo;
self.bar = bar;
};
generated.A.prototype.x = function(baz) {
var self = this;
return self.bar.charAt(baz).toString();
};
/** @constructor*/
generated.B = function(foo,bar) {
var self = this;
generated.A.call(self,foo,bar);
};
goog.inherits(generated.B, generated.A);
generated.B.prototype.y = function(baz) {
var self = this;
return baz.charAt(self.foo).toString();
};
