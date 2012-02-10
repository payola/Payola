goog.provide('scala.NoSuchElementException');
goog.require('scala.RuntimeException');
scala.NoSuchElementException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
self.message = undefined;
self.cause = undefined;
goog.base(self, message, cause);};
goog.inherits(scala.NoSuchElementException, scala.RuntimeException);
scala.NoSuchElementException.prototype.metaClass_ = new s2js.MetaClass('scala.NoSuchElementException', [scala.RuntimeException]);
