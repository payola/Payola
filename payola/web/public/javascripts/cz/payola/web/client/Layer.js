goog.provide('cz.payola.web.client.Layer');
goog.require('cz.payola.web.client.Vector');
cz.payola.web.client.Layer = function(canvas, context) {
var self = this;
self.canvas = canvas;
self.context = context;
};
cz.payola.web.client.Layer.prototype.setSize = function(size) {
var self = this;
self.canvas.width = size.x;
self.canvas.height = size.y;
};
cz.payola.web.client.Layer.prototype.getSize = function() {
var self = this;
return new cz.payola.web.client.Vector(self.canvas.width, self.canvas.height);
};
cz.payola.web.client.Layer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Layer', []);
