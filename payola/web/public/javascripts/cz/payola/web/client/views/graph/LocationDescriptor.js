goog.provide('cz.payola.web.client.views.graph.LocationDescriptor');
goog.require('cz.payola.web.client.views.Point');
goog.require('cz.payola.web.client.views.Vector');
cz.payola.web.client.views.graph.LocationDescriptor.informationPositionCorrection = new cz.payola.web.client.views.Vector(0.0, 4.0);
cz.payola.web.client.views.graph.LocationDescriptor.getVertexInformationPosition = function(position) {
var self = this;
return position.$plus(self.informationPositionCorrection);
};
cz.payola.web.client.views.graph.LocationDescriptor.getEdgeInformationPosition = function(originPosition, destinationPosition) {
var self = this;
var x = ((originPosition.x + destinationPosition.x) / 2);
var y = ((originPosition.y + destinationPosition.y) / 2);
return new cz.payola.web.client.views.Point(x, y).$plus(self.informationPositionCorrection);
};
cz.payola.web.client.views.graph.LocationDescriptor.metaClass_ = new s2js.MetaClass('cz.payola.web.client.views.graph.LocationDescriptor', []);
