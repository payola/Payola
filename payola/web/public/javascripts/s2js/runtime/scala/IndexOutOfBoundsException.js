goog.provide('scala.IndexOutOfBoundsException');
goog.require('scala.RuntimeException');
scala.IndexOutOfBoundsException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
self.message = undefined;
self.cause = undefined;
goog.base(self, message, cause);};
goog.inherits(scala.IndexOutOfBoundsException, scala.RuntimeException);
scala.IndexOutOfBoundsException.prototype.metaClass_ = new s2js.MetaClass('scala.IndexOutOfBoundsException', [scala.RuntimeException]);
