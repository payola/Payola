goog.provide('scala.util.control.BreakControlException');
goog.provide('scala.util.control.Breaks');
goog.require('scala.Exception');
scala.util.control.BreakControlException = function() {
var self = this;
goog.base(self);
};
goog.inherits(scala.util.control.BreakControlException, scala.Exception);
scala.util.control.BreakControlException.prototype.__class__ = new s2js.Class('scala.util.control.BreakControlException', [scala.Exception]);
scala.util.control.Breaks.breakable = function(op) {
var self = this;
try {
op();
} catch ($ex$1) {
(function() {
if (s2js.isInstanceOf($ex$1, 'scala.util.control.BreakControlException')) {

return;
}
if (true) {
var ex = $ex$1;
(function() {
throw ex;
})();
return;
}
throw $ex$1;
})();
}

};
scala.util.control.Breaks.$break = function() {
var self = this;
(function() {
throw new scala.util.control.BreakControlException();
})();
};
scala.util.control.Breaks.__class__ = new s2js.Class('scala.util.control.Breaks', []);
