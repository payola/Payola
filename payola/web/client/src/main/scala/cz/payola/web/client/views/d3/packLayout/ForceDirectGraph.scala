
package cz.payola.web.client.views.d3.packLayout

import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.PluginView
import s2js.adapters.html
import s2js.compiler.javascript
import cz.payola.web.shared.transformators.RdfJsonTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * @author Vit Samek
 */
class ForceDirectGraph(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("ForceDirect Graph", prefixApplier) {

    def supportedDataFormat: String = "RDF/JSON"
    //    def supportedDataFormat: String = "RDF/JSON"


    val d3Placeholder = new Div(List())
    val chart = new Div(List())
    val property = new Div(List())

    chart.setAttribute("id","chart")
    property.setAttribute("id","property")

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
       $('#property').append('<h3>Properties</h3>').append('<div id="prop"><div>');
       $('#property').css("word-break","break-all");

       var WIDTH = 900;
       var HEIGHT = 900;

    function parse(json,bigGraph){

        var mainId = Object.keys(json);
        var id;
        var index = 0;
        var hashMapID = [];
        var subKeys;
        var nodes  = [];
        var links = [];
        var qeueu = [];
        var propertiesQeueu = [];
        var group = 1;
        var bigGraph = bigGraph;

        var node = {"name": "","group": 1 ,"properties": [], "line": []};
        for (key in mainId) {
            id = mainId[key];
            if (!hashMapID[id]) {
                hashMapID[id] = index++;
            }
            node.name = id;
            node.group = group++;
            var tmp = json[id];
            subKeys = Object.keys(tmp);
            for (i in subKeys)
            {
                var obj = tmp[subKeys[i]][0];
                if (obj)
                {
                    if (obj["type"] === "literal") {
                        if(bigGraph)
                        {
                            propertiesQeueu.push({"source":id,"target":{"name":subKeys[i],"value":obj["value"]}});
                        }
                        node.properties.push({"name":subKeys[i],"value":obj["value"]});
                    }
                    else if (obj["type"] === "uri")
                    {
                        var idtmp = hashMapID[obj["value"]];
                        qeueu.push({"source":id,"target":obj["value"],property:false});
                        node.line.push(obj["value"]);
                    }
                }
            }
            nodes.push({"name":node.name,"properties":node.properties, "group":node.group});
            node = {"name": "","group": 1 ,"properties": [], "line": []};
        }
        connect();
        conectProperty();

        function conectProperty()
        {
             for(k in propertiesQeueu)
             {
               var objectToConenct = propertiesQeueu[k];
               var source = hashMapID[objectToConenct.source];
               nodes.push({"name":objectToConenct.target.name,"properties":[{"value":objectToConenct.target.value}],"group":group++});
               links.push({"source":source, "target":index++, "value":1});
             }
        };

        function connect()
        {
            for(k in qeueu)
            {
                var objectToConnect = qeueu[k];
                var source  = hashMapID[objectToConnect.source];
                var target = hashMapID[objectToConnect.target];
                if(!target)
                {
                    hashMapID[objectToConnect.target] = index++;
                    nodes.push({"name":objectToConnect.target,"properties":[] ,"group":group++});
                }
                links.push({"source":source, "target":hashMapID[objectToConnect.target], "value":1});
            }
        }
        return {"nodes":nodes,"links":links};
    };

    var graph = parse(json,true);

    var w = WIDTH,
    h = HEIGHT,
    fill = d3.scale.category20(); // color

    var vis = d3.select("#chart") //svg
        .append("svg")
        .attr("width", w)
        .attr("height", h)
        .attr("pointer-events", "all")
        .append('svg:g')
    .call(d3.behavior.zoom().on("zoom", redraw))
        .append('svg:g');

    vis.append('svg:rect')
    .attr('width', w)
    .attr('height', h)
    .attr('fill', 'white');

    var node,
    nodeFontSize = 14;

    function redraw() {

    var ar = d3.event.translate;

    var tmp = ar[0]+ ","+ ar[1];
        vis.attr("transform",
            "translate(" + tmp + ")"
                + " scale(" + d3.event.scale + ")");
    }

    var draw = function(json) {
        var force = d3.layout.force()
            .charge(-120)
            .linkDistance(30)
            .nodes(json.nodes)
            .links(json.links)
            .size([w, h])
        .start();

        var link = vis.selectAll(".link")
            .data(json.links)
            .enter().append("line")
            .attr("class", "link")
            .style("stroke-width", function(d) { return Math.sqrt(0.2); })
            .style("stroke","#000")
            .attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node = vis.selectAll(".node")
            .data(json.nodes)
            .enter().append("circle")
            .attr("class", "node")
            .attr("r",3)
            .style("fill", "#1C2194")
            .call(force.drag);


        node.append("title")
            .text(function(d) { return d.name; });

        node.append("properties").text(function(d){
            var str = "";

            for(var i = 0; i < d.properties.length;++i)
            {
                str = str+ d.properties[i].name + ":=" + d.properties[i].value;
                if(i != d.properties.length-1){
                    str += ";"
                };
            }
            return str.trim();
        })

        force.on("tick", function() {
            link.attr("x1", function(d) { return d.source.x; })
                .attr("y1", function(d) { return d.source.y; })
                .attr("x2", function(d) { return d.target.x; })
                .attr("y2", function(d) { return d.target.y; });

            node.attr("cx", function(d) { return d.x; })
                .attr("cy", function(d) { return d.y; });
        });
    };
           draw(graph);

    $('.node').live('click',function(){

          $("circle[style*='stroke']").css('stroke','');
          $(this).css('stroke','red');
          $('#prop').empty();

          var title = $(this).children()[0].innerHTML;
          var text = $(this).children()[1].innerHTML;

        if(title.length != 0)
        {
            $('#prop').append('<h5>Node name:</h5><p>'+ title+'</p>');
        }

        if(text.length != 0)
        {
           var array = text.split(";");
           var values = array.map(function(d){
           var tmp =  d.split(":=");
           var b = tmp[0];
               if (b !== "undefined")
               {
                    $('#prop').append('<h4>Property name:</h4><p>'+tmp[0]+'</p>');
               }
               $('#prop').append('<b>Property value:</b><span>'+tmp[1]+'</span>');
           });
        }
    });
        """)
    def parseJSON(json: Any) {}

    def createSubViews = List(d3Placeholder,property,chart)

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Any] => Unit) {
        RdfJsonTransformator.getCompleteGraph(evaluationId)(updateGraph(_)) //TODO default graph and paginating
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        success() //TODO whe is available????
    }

    override def render(parent: html.Element) {
        subViews.foreach { v =>
            new Text(" ").render(parent)
            v.render(parent)
        }
        parseJSON(_serializedGraph)
        _rendered = true
    }
}
