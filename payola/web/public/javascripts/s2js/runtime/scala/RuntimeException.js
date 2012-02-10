goog.provide('scala.RuntimeException');
goog.require('scala.Exception');
scala.RuntimeException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
self.message = undefined;
self.cause = undefined;
goog.base(self, message, cause);};
goog.inherits(scala.RuntimeException, scala.Exception);
scala.RuntimeException.prototype.metaClass_ = new s2js.MetaClass('scala.RuntimeException', [scala.Exception]);
