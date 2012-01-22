goog.provide('cz.payola.web.client.views.Layer');
goog.require('cz.payola.web.client.views.Vector');
cz.payola.web.client.views.Layer = function(canvas, context) {
var self = this;
self.canvas = canvas;
self.context = context;
self.cleared = true;
};
cz.payola.web.client.views.Layer.prototype.setSize = function(size) {
var self = this;
self.canvas.width = size.x;
self.canvas.height = size.y;
};
cz.payola.web.client.views.Layer.prototype.getSize = function() {
var self = this;
return new cz.payola.web.client.views.Vector(self.canvas.width, self.canvas.height);
};
cz.payola.web.client.views.Layer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.Layer', []);
