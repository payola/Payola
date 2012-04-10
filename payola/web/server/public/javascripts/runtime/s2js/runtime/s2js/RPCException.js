goog.provide('s2js.RPCException');
goog.require('s2js.Exception');
s2js.RPCException = function(message, cause) {
var self = this;
if (typeof(message) === 'undefined') { message = ''; }
if (typeof(cause) === 'undefined') { cause = null; }
self.message = message;
self.cause = cause;
goog.base(self, message, cause);
};
goog.inherits(s2js.RPCException, s2js.Exception);
s2js.RPCException.prototype.__class__ = new s2js.Class('s2js.RPCException', [s2js.Exception]);
