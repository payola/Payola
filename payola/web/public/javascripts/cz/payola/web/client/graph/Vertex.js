goog.provide('cz.payola.web.client.graph.Vertex');
cz.payola.web.client.graph.Vertex = function(id, x, y, text, neighbours) {
var self = this;
self.id = id;
self.x = x;
self.y = y;
self.text = text;
self.neighbours = neighbours;
self.selected = false;
};
cz.payola.web.client.graph.Vertex.prototype.metaClass_ = new s2js.MetaClass('cz.payola.web.client.graph.Vertex', []);
