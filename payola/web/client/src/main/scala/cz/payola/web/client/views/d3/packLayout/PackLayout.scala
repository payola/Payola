package cz.payola.web.client.views.d3.packLayout

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.PluginView
import s2js.compiler.javascript
import s2js.adapters.browser.`package`._
import s2js.adapters.html
import cz.payola.web.shared.transformators.RdfJsonTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * @author Jiri Helmich
 */
class PackLayout(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("Pack Layout", prefixApplier) {

    val d3Placeholder = new Div(List())
    val placeholder = new Div(List(d3Placeholder))
    d3Placeholder.setAttribute("id","d3-placeholder")

    private var _serializedGraph : Any = ""
    private var _rendered = false

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0


    override def updateSerializedGraph(serializedGraph: Option[Any]) {
        serializedGraph.map{ sg =>
            _serializedGraph = sg
            d3Placeholder.removeAllChildNodes()
            if(_rendered) { parseJSON(_serializedGraph) }
        }
    }

    @javascript(
        """
            var skos = function(s){ return "http://www.w3.org/2004/02/skos/core#"+s; };
            var rdf = function(s) { return "http://www.w3.org/1999/02/22-rdf-syntax-ns#"+s; };

            var objMap = {};
            var queue = [];

            var data = null;

            for(var i in json){
                var entity = json[i];

                if (entity[rdf("type")] && (entity[rdf("type")][0].value == skos("Concept"))){

                    function getName(entity){
                        if(entity[skos("prefLabel")] && entity[skos("prefLabel")][0] && entity[skos("prefLabel")][0].value){
                            return entity[skos("prefLabel")][0].value;
                        }
                        return "no name";
                    }

                    var o = {
                        name: getName(entity),
                    };

                    if(entity[skos("broader")]){
                        for(var n in entity[skos("broader")]){
                            queue.push({entity:entity, child: o, callback: function(entity, child){
                                var obj = objMap[entity[skos("broader")][n].value];
                                if(!obj) return;
                                if(!obj.children) { obj.children = []; }
                                obj.children.push(child);
                            }});
                        }
                    }

                    if(entity[rdf("value")]){
                        o.size = (((entity[rdf("value")] || [{value:1}])[0]) || {value: 1}).value;
                    }else{
                        o.size = 1;
                    }

                    if (!entity[skos("broader")] && !entity[rdf("value")]){
                        data = o;
                    }

                    objMap[i] = o;
                }
            };

            for (var c in queue){
                var q = queue[c];
                q.callback(q.entity, q.child);
            }

          var w = 1280,
              h = 800,
              r = 720,
              x = d3.scale.linear().range([0, r]),
              y = d3.scale.linear().range([0, r]),
              node,
              root;

          var pack = d3.layout.pack()
              .size([r, r])
              .value(function(d) { return d.size; })

          var vis = d3.select(self.d3Placeholder.blockHtmlElement()).insert("svg:svg", "h2")
              .attr("width", w)
              .attr("height", h)
            .append("svg:g")
              .attr("transform", "translate(" + (w - r) / 2 + "," + (h - r) / 2 + ")");

          //d3.json("flare.json", function(data) {
            node = root = data;

            var nodes = pack.nodes(root);

            vis.selectAll("circle")
                .data(nodes)
              .enter().append("svg:circle")
                .attr("class", function(d) { return d.children ? "parent" : "child"; })
                .attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; })
                .attr("r", function(d) { return d.r; })
                .on("click", function(d) { return zoom(node == d ? root : d); });

            vis.selectAll("text")
                .data(nodes)
              .enter().append("svg:text")
                .attr("class", function(d) { return d.children ? "parent" : "child"; })
                .attr("x", function(d) { return d.x; })
                .attr("y", function(d) { return d.y; })
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")
                .style("opacity", function(d) { return d.r > 20 ? 1 : 0; })
                .text(function(d) { return d.name; });

            d3.select(window).on("click", function() { zoom(root); });
          //});

          function zoom(d, i) {
            var k = r / d.r / 2;
            x.domain([d.x - d.r, d.x + d.r]);
            y.domain([d.y - d.r, d.y + d.r]);

            var t = vis.transition()
                .duration(d3.event.altKey ? 7500 : 750);

            t.selectAll("circle")
                .attr("cx", function(d) { return x(d.x); })
                .attr("cy", function(d) { return y(d.y); })
                .attr("r", function(d) { return k * d.r; });

            t.selectAll("text")
                .attr("x", function(d) { return x(d.x); })
                .attr("y", function(d) { return y(d.y); })
                .style("opacity", function(d) { return k * d.r > 20 ? 1 : 0; });

            node = d;
            d3.event.stopPropagation();
          }


        """)
    def parseJSON(json: Any) {}

    def createSubViews = List(placeholder)

    override def render(parent: html.Element) {
        subViews.foreach { v =>
            new Text(" ").render(parent)
            v.render(parent)
        }
        parseJSON(_serializedGraph)
        _rendered = true
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        success() //TODO whe is available????
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Any] => Unit) {
        RdfJsonTransformator.getCompleteGraph(evaluationId)(updateGraph(_)) //TODO default graph and paginating
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
