goog.provide('cz.payola.web.client.graph.Color');
cz.payola.web.client.graph.Color = function(red, green, blue, alpha) {
var self = this;
if (typeof(alpha) === 'undefined') { alpha = 1.0; }
self.red = red;
self.green = green;
self.blue = blue;
self.alpha = alpha;
self.red = undefined;
self.green = undefined;
self.blue = undefined;
self.alpha = undefined;
};
cz.payola.web.client.graph.Color.prototype.toString = function() {
var self = this;
return (((((((('rgba(' + self.red) + ', ') + self.green) + ', ') + self.blue) + ', ') + self.alpha) + ')');
};
cz.payola.web.client.graph.Color.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Color', []);
