goog.provide('cz.payola.web.client.views.graph.View');
cz.payola.web.client.views.graph.View = function() {
var self = this;
};
cz.payola.web.client.views.graph.View.prototype.drawRoundedRectangle = function(context, position, size, radius) {
var self = this;
context.beginPath();
var aX = (position.x + radius);
var aY = position.y;
context.moveTo(aX, aY);
aX = position.x;
aY = position.y;
context.quadraticCurveTo(aX, aY, aX, (aY + radius));
aX = position.x;
aY = (position.y + size.y);
context.lineTo(aX, (aY - radius));
context.quadraticCurveTo(aX, aY, (aX + radius), aY);
aX = (position.x + size.x);
aY = (position.y + size.y);
context.lineTo((aX - radius), aY);
context.quadraticCurveTo(aX, aY, aX, (aY - radius));
aX = (position.x + size.x);
aY = position.y;
context.lineTo(aX, (aY + radius));
context.quadraticCurveTo(aX, aY, (aX - radius), aY);
context.closePath();
};
cz.payola.web.client.views.graph.View.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.View', []);
