goog.provide('cz.payola.web.client.graph.Vertex');
cz.payola.web.client.graph.Vertex = function(id, position, text, neighbours) {
var self = this;
self.id = id;
self.position = position;
self.text = text;
self.neighbours = neighbours;
self.selected = false;
};
cz.payola.web.client.graph.Vertex.prototype.x = function() {
var self = this;
return self.position.x;
};
cz.payola.web.client.graph.Vertex.prototype.y = function() {
var self = this;
return self.position.y;
};
cz.payola.web.client.graph.Vertex.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Vertex', []);
