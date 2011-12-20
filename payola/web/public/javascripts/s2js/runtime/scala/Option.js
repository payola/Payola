goog.provide('scala.None');
goog.provide('scala.Option');
goog.provide('scala.Some');
goog.require('scala.IndexOutOfBoundsException');
goog.require('scala.NoSuchElementException');
goog.require('scala.Product');
scala.Option = function() {
var self = this;
self.$self = undefined;
goog.object.extend(self, new scala.Product());
};
scala.Option.prototype.isDefined = function() {
var self = this;
return (! self.isEmpty());
};
scala.Option.prototype.getOrElse = function($default) {
var self = this;
return (function() {
if (self.isEmpty()) {
return $default;
} else {
return self.get();
}})();
};
scala.Option.prototype.orNull = function(ev) {
var self = this;
return self.getOrElse(ev(null));
};
scala.Option.prototype.map = function(f) {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return new scala.Some(f(self.get()));
}})();
};
scala.Option.prototype.flatMap = function(f) {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return f(self.get());
}})();
};
scala.Option.prototype.flatten = function(ev) {
var self = this;
return (function() {
if (self.isEmpty()) {
return scala.None;
} else {
return ev(self.get());
}})();
};
scala.Option.prototype.filter = function(p) {
var self = this;
return (function() {
if ((self.isEmpty() || p(self.get()))) {
return self;
} else {
return scala.None;
}})();
};
scala.Option.prototype.filterNot = function(p) {
var self = this;
return (function() {
if ((self.isEmpty() || (! p(self.get())))) {
return self;
} else {
return scala.None;
}})();
};
scala.Option.prototype.nonEmpty = function() {
var self = this;
return self.isDefined();
};
scala.Option.prototype.exists = function(p) {
var self = this;
return ((! self.isEmpty()) && p(self.get()));
};
scala.Option.prototype.foreach = function(f) {
var self = this;
(function() {
if ((! self.isEmpty())) {
f(self.get());
} else {
}})();
};
scala.Option.prototype.collect = function(pf) {
var self = this;
return (function() {
if (((! self.isEmpty()) && pf.isDefinedAt(self.get()))) {
return new scala.Some(pf(self.get()));
} else {
return scala.None;
}})();
};
scala.Option.prototype.orElse = function(alternative) {
var self = this;
return (function() {
if (self.isEmpty()) {
return alternative;
} else {
return self;
}})();
};
scala.Option.prototype.metaClass_ = new s2js.MetaClass('scala.Option', [scala.Product]);
scala.Some = function(x) {
var self = this;
self.x = x;
goog.base(self);goog.object.extend(self, new scala.Product());
};
goog.inherits(scala.Some, scala.Option);
scala.Some.prototype.isEmpty = function() {
var self = this;
return false;
};
scala.Some.prototype.get = function() {
var self = this;
return self.x;
};
scala.Some.prototype.copy = function(x) {
var self = this;
if (typeof(x) === 'undefined') { x = self.x; }
return new scala.Some(x);
};
scala.Some.prototype.productPrefix = function() {
var self = this;
return 'Some';
};
scala.Some.prototype.productArity = function() {
var self = this;
return 1;
};
scala.Some.prototype.productElement = function($x$1) {
var self = this;
return (function($selector_1) {
if ($selector_1 === 0) {
return scala.Some.x;
}
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
scala.Some.prototype.metaClass_ = new s2js.MetaClass('scala.Some', [scala.Option, scala.Product]);
goog.object.extend(scala.None, new scala.Option());
goog.object.extend(scala.None, new scala.Product());
scala.None.isEmpty = function() {
var self = this;
return true;
};
scala.None.get = function() {
var self = this;
return (function() {
throw new scala.NoSuchElementException('None.get');
})();
};
scala.None.productPrefix = function() {
var self = this;
return 'None';
};
scala.None.productArity = function() {
var self = this;
return 0;
};
scala.None.productElement = function($x$1) {
var self = this;
return (function($selector_2) {
if (true) {
return (function() {
throw new scala.IndexOutOfBoundsException($x$1.toString());
})();
}
})($x$1);
};
scala.None.metaClass_ = new s2js.MetaClass('scala.None', [scala.Option, scala.Product]);
scala.Option.option2Iterable = function(xo) {
var self = this;
return self.option2Iterable(xo).toList();
};
scala.Option.$apply = function(x) {
var self = this;
return (function() {
if ((x == null)) {
return scala.None;
} else {
return new scala.Some(x);
}})();
};
scala.Option.empty = function() {
var self = this;
return scala.None;
};
scala.Option.metaClass_ = new s2js.MetaClass('scala.Option', []);
scala.Some.unapply = function(x$0) {
var self = this;
return (function() {
if ((x$0 == null)) {
return scala.None;
} else {
return new scala.Some(x$0.x);
}})();
};
scala.Some.$apply = function(x) {
var self = this;
return new scala.Some(x);
};
scala.Some.metaClass_ = new s2js.MetaClass('scala.Some', []);
