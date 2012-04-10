goog.provide('scala.ClassCastException');
goog.require('scala.Exception');
scala.ClassCastException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(scala.ClassCastException, scala.Exception);
scala.ClassCastException.prototype.__class__ = new s2js.Class('scala.ClassCastException', [scala.Exception]);
