goog.provide('cz.payola.web.client.Layer');
cz.payola.web.client.Layer = function(canvas, context) {
var self = this;
self.canvas = canvas;
self.context = context;
};
cz.payola.web.client.Layer.prototype.setWidth = function(newWidth) {
var self = this;
self.canvas.width = newWidth;
};
cz.payola.web.client.Layer.prototype.setHeight = function(newHeight) {
var self = this;
self.canvas.height = newHeight;
};
cz.payola.web.client.Layer.prototype.getWidth = function() {
var self = this;
return self.canvas.width;
};
cz.payola.web.client.Layer.prototype.getHeight = function() {
var self = this;
return self.canvas.height;
};
cz.payola.web.client.Layer.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.Layer', []);
