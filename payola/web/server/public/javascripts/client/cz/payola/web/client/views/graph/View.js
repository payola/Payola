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
cz.payola.web.client.views.graph.View.prototype.drawBezierCurve = function(context, control1, control2, origin, destination, lineWidth, color) {
var self = this;
context.lineWidth = lineWidth;
context.strokeStyle = color.toString();
context.beginPath();
context.moveTo(origin.x, origin.y);
context.bezierCurveTo(control1.x, control1.y, control2.x, control2.y, destination.x, destination.y);
context.stroke();

};
cz.payola.web.client.views.graph.View.prototype.drawStraightLine = function(context, origin, destination, lineWidth, color) {
var self = this;
context.lineWidth = lineWidth;
context.strokeStyle = color.toString();
context.beginPath();
context.moveTo(origin.x, origin.y);
context.lineTo(destination.x, destination.y);
context.stroke();

};
cz.payola.web.client.views.graph.View.prototype.drawText = function(context, text, origin, color, font, align) {
var self = this;
context.fillStyle = color.toString();
context.font = font;
context.textAlign = align;
context.fillText(text, origin.x, origin.y);

};
cz.payola.web.client.views.graph.View.prototype.fillCurrentSpace = function(context, color) {
var self = this;
context.fillStyle = color.toString();
context.fill();

};
cz.payola.web.client.views.graph.View.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.View', []);
