goog.provide('scala.IndexOutOfBoundsException');
goog.require('scala.RuntimeException');
scala.IndexOutOfBoundsException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(scala.IndexOutOfBoundsException, scala.RuntimeException);
scala.IndexOutOfBoundsException.prototype.__class__ = new s2js.Class('scala.IndexOutOfBoundsException', [scala.RuntimeException]);
