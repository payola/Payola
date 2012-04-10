goog.provide('scala.UnsupportedOperationException');
goog.require('scala.RuntimeException');
scala.UnsupportedOperationException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(scala.UnsupportedOperationException, scala.RuntimeException);
scala.UnsupportedOperationException.prototype.__class__ = new s2js.Class('scala.UnsupportedOperationException', [scala.RuntimeException]);
