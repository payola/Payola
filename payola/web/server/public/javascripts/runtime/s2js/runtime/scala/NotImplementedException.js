goog.provide('scala.NotImplementedException');
goog.require('scala.RuntimeException');
scala.NotImplementedException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(scala.NotImplementedException, scala.RuntimeException);
scala.NotImplementedException.prototype.__class__ = new s2js.Class('scala.NotImplementedException', [scala.RuntimeException]);
