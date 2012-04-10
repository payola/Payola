goog.provide('scala.RuntimeException');
goog.require('scala.Exception');
scala.RuntimeException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(scala.RuntimeException, scala.Exception);
scala.RuntimeException.prototype.__class__ = new s2js.Class('scala.RuntimeException', [scala.Exception]);
