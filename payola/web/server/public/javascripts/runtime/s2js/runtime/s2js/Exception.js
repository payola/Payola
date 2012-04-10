goog.provide('s2js.Exception');
s2js.Exception = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
};
s2js.Exception.prototype.__class__ = new s2js.Class('s2js.Exception', []);
