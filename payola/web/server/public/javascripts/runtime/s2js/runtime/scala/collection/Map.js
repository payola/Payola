goog.provide('scala.collection.Map');
goog.require('scala.NoSuchElementException');
goog.require('scala.None');
goog.require('scala.Option');
goog.require('scala.Tuple2');
goog.require('scala.UnsupportedOperationException');
goog.require('scala.collection.Iterable');
goog.require('scala.collection.mutable.ListBuffer');
scala.collection.Map = function() {
var self = this;
self.internalJsObject = {};
};
goog.inherits(scala.collection.Map, scala.collection.Iterable);
scala.collection.Map.prototype.foreach = function(f) {
var self = this;

        for (var key in self.internalJsObject) {
            if (self.internalJsObject.hasOwnProperty(key)) {
                f(new scala.Tuple2(key, self.internalJsObject[key]))
            }
        }
    };
scala.collection.Map.prototype.$plus$eq = function(x) {
var self = this;
self.internalJsObject[x._1] = x._2;};
scala.collection.Map.prototype.prepend = function(x) {
var self = this;
(function() {
throw new scala.UnsupportedOperationException('Can\'t prepend to a Map.');
})();
};
scala.collection.Map.prototype.reversed = function() {
var self = this;
return scala.collection.mutable.ListBuffer.empty().$plus$plus(self).reversed();
};
scala.collection.Map.prototype.$minus$eq = function(x) {
var self = this;
delete self.internalJsObject[x];};
scala.collection.Map.prototype.get = function(key) {
var self = this;

        if (s2js.isUndefined(self.internalJsObject[key])) {
            return scala.None;
        } else {
            return new scala.Some(self.internalJsObject[key]);
        }
    };
scala.collection.Map.prototype.getOrElse = function(key, $default) {
var self = this;
return (function($selector$1) {
if (s2js.isInstanceOf($selector$1, 'scala.Some') && (true)) {
var v = $selector$1.productElement(0);
return v;
}
if ($selector$1 === scala.None) {
return $default;
}
})(self.get(key));
};
scala.collection.Map.prototype.$apply = function(key) {
var self = this;
return (function($selector$2) {
if ($selector$2 === scala.None) {
return self.$default(key);
}
if (s2js.isInstanceOf($selector$2, 'scala.Some') && (true)) {
var value = $selector$2.productElement(0);
return value;
}
})(self.get(key));
};
scala.collection.Map.prototype.contains = function(key) {
var self = this;
return self.get(key).isDefined();
};
scala.collection.Map.prototype.isDefinedAt = function(key) {
var self = this;
return self.contains(key);
};
scala.collection.Map.prototype.$default = function(key) {
var self = this;
return (function() {
throw new scala.NoSuchElementException(('key not found: ' + key));
})();
};
scala.collection.Map.prototype.put = function(key, value) {
var self = this;
var r = self.get(key);
self.update(key, value);
return r;

};
scala.collection.Map.prototype.update = function(key, value) {
var self = this;
self.$plus$eq(new scala.Tuple2(key, value));
};
scala.collection.Map.prototype.remove = function(key) {
var self = this;
var r = self.get(key);
self.$minus$eq(key);
return r;

};
scala.collection.Map.prototype.getOrElseUpdate = function(key, op) {
var self = this;
return (function($selector$3) {
if (s2js.isInstanceOf($selector$3, 'scala.Some') && (true)) {
var v = $selector$3.productElement(0);
return v;
}
if ($selector$3 === scala.None) {
var d = op;
self.update(key, d);
return d;

}
})(self.get(key));
};
scala.collection.Map.prototype.__class__ = new s2js.Class('scala.collection.Map', [scala.collection.Iterable]);
