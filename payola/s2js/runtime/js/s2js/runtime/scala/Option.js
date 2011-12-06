goog.provide('scala.None');
goog.provide('scala.Option');
goog.provide('scala.Some');
goog.require('scala.NoSuchElementException');
goog.require('scala.collection.Iterator');
goog.require('scala.collection.immutable.List');
scala.Option = function() {
var self = this;
};
scala.Option.prototype.isDefined = function() {
var self = this;
return (! self.isEmpty);
};
scala.Option.prototype.getOrElse = function(s2js_default) {
var self = this;
return (function() {
if (self.isEmpty) {
return s2js_default;
} else {
return self.get;
}})();
};
scala.Option.prototype.orNull = function(ev) {
var self = this;
return self.getOrElse(ev(null));
};
scala.Option.prototype.map = function(f) {
var self = this;
return (function() {
if (self.isEmpty) {
return scala.None;
} else {
return scala.Some.s2js_apply(f(self.get));
}})();
};
scala.Option.prototype.flatMap = function(f) {
var self = this;
return (function() {
if (self.isEmpty) {
return scala.None;
} else {
return f(self.get);
}})();
};
scala.Option.prototype.flatten = function(ev) {
var self = this;
return (function() {
if (self.isEmpty) {
return scala.None;
} else {
return ev(self.get);
}})();
};
scala.Option.prototype.filter = function(p) {
var self = this;
return (function() {
if ((self.isEmpty || p(self.get))) {
return self;
} else {
return scala.None;
}})();
};
scala.Option.prototype.filterNot = function(p) {
var self = this;
return (function() {
if ((self.isEmpty || (! p(self.get)))) {
return self;
} else {
return scala.None;
}})();
};
scala.Option.prototype.exists = function(p) {
var self = this;
return ((! self.isEmpty) && p(self.get));
};
scala.Option.prototype.foreach = function(f) {
var self = this;
(function() {
if ((! self.isEmpty)) {
f(self.get);
} else {
}})();
};
scala.Option.prototype.orElse = function(alternative) {
var self = this;
return (function() {
if (self.isEmpty) {
return alternative;
} else {
return self;
}})();
};
scala.Option.prototype.iterator = function() {
var self = this;
return (function() {
if (self.isEmpty) {
return scala.collection.Iterator.empty;
} else {
return scala.collection.Iterator.single(self.get);
}})();
};
scala.Option.prototype.toList = function() {
var self = this;
return (function() {
if (self.isEmpty) {
return self.Nil;
} else {
return self.List.s2js_apply(self.get);
}})();
};
scala.Option.prototype.metaClass_ = new s2js.MetaClass('scala.Option', []);
scala.Some = function(x) {
var self = this;
self.x = x;
goog.base(self);};
goog.inherits(scala.Some, scala.Option);
scala.Some.prototype.isEmpty = function() {
var self = this;
return false;
};
scala.Some.prototype.get = function() {
var self = this;
return self.x;
};
scala.Some.prototype.metaClass_ = new s2js.MetaClass('scala.Some', [scala.Option]);
if (typeof(scala.None) === 'undefined') { scala.None = {}; }
goog.object.extend(scala.None, new scala.Option());
scala.None.isEmpty = function() {
var self = this;
return true;
};
scala.None.get = function() {
var self = this;
return throw new scala.NoSuchElementException('None.get');
};
scala.None.metaClass_ = new s2js.MetaClass('scala.None', [scala.Option]);
if (typeof(scala.Option) === 'undefined') { scala.Option = {}; }
scala.Option.s2js_apply = function(x) {
var self = this;
return (function() {
if ((x == null)) {
return scala.None;
} else {
return scala.Some.s2js_apply(x);
}})();
};
scala.Option.empty = function() {
var self = this;
return scala.None;
};
scala.Option.metaClass_ = new s2js.MetaClass('scala.Option', []);
if (typeof(scala.Some) === 'undefined') { scala.Some = {}; }
scala.Some.s2js_apply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Some.metaClass_ = new s2js.MetaClass('scala.Some', []);
