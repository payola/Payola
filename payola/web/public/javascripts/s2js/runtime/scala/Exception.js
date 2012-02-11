goog.provide('scala.Exception');
scala.Exception = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
};
scala.Exception.prototype.metaClass_ = new s2js.MetaClass('scala.Exception', []);
