package s2js.adapters.js.sigma

import s2js.adapters.html.Element

class Sigma {
    @native def init(element: Element): Sigma

    @native def kill()

    @native var id: String = ""

    // Config:
    /*def configProperties(a1, a2): {
        var res = s.config(a1, a2);
        return res == s ? self : res;
    }*/

    @native def drawingProperties(a1: DrawingProperties): Sigma
    //def drawingProperties(a1: DrawProperties, a2): Sigma = null

    /*def mouseProperties(a1, a2): {
        var res = s.mousecaptor.config(a1, a2);
        return res == s.mousecaptor ? self : res;
    };*/
    @native def graphProperties(a1: GraphProperties): Sigma
    //def graphProperties(a1: GraphProperties, a2): Sigma = null

    /*def getMouse: {
        return {
            mouseX: s.mousecaptor.mouseX,
            mouseY: s.mousecaptor.mouseY,
            down: s.mousecaptor.isMouseDown
        };
    };*/

    // Actions:
    /*def position(stageX, stageY, ratio): Sigma = null{
        if (arguments.length == 0) {
            return {
                stageX: s.mousecaptor.stageX,
                stageY: s.mousecaptor.stageY,
                ratio: s.mousecaptor.ratio
            };
        }else {
            s.mousecaptor.stageX = stageX != undefined ?
                stageX :
                s.mousecaptor.stageX;
            s.mousecaptor.stageY = stageY != undefined ?
                stageY :
                s.mousecaptor.stageY;
            s.mousecaptor.ratio = ratio != undefined ?
                ratio :
                s.mousecaptor.ratio;

            return self;
        }
    };*/

    /*def goTo(stageX, stageY, ratio): Sigma = null{
        s.mousecaptor.interpolate(stageX, stageY, ratio);
        return self;
    };*/

    @native def zoomTo(x: Int, y: Int, ratio: Double): Sigma

    @native def resize(w: Int, h: Int): Sigma

    /**
     * Redraws the sigma plugin.
     */
    @native def refresh(): Sigma

    // Tasks methods:
    /*def addGenerator(id, task, condition): Sigma = null {
        sigma_delete.chronos.addGenerator(id + '_ext_' + s.id, task, condition);
        return self;
    };*/

    @native def removeGenerator(id: String): Sigma

    // Graph methods:
    /**
     * Append a new vertex to the graph
     * @param id Name of the new vertex
     * @param params Vertex config
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def addNode(id: String, params: NodeProperties): Sigma

    /**
     * Appends a new edge to the graph
     * @param id Name of the new edge
     * @param source Name of the source vertex
     * @param target Name of the destination vertex
     * @param params Edge config
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def addEdge(id: String, source: String, target: String, params: EdgeProperties): Sigma

    @native def dropNode(v: String): Sigma

    @native def dropEdge(v: String): Sigma

    /*def pushGraph(object, safe: Boolean): Sigma = null {
        object.nodes && object.nodes.forEach(function(node) {
            node['id'] && (!safe || !s.graph.nodesIndex[node['id']]) &&
            self.addNode(node['id'], node);
        });

        var isEdgeValid;
        object.edges && object.edges.forEach(function(edge) {
            validID = edge['source'] && edge['target'] && edge['id'];
            validID &&
                (!safe || !s.graph.edgesIndex[edge['id']]) &&
            self.addNode(
                edge['id'],
            edge['source'],
            edge['target'],
            edge
            );
        });

        return self;
    };*/

    /**
     * Removes all added vertices and edges.
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def emptyGraph: Sigma

    @native def getNodesCount: Int

    @native def getEdgesCount: Int

    @native def iterNodes(fun: (Node)=> Unit): Sigma
    //@native def iterNodes(fun: (String)=> Unit, ids: String*): Sigma

    @native def iterEdges(fun: (Edge)=> Unit): Sigma
    //@native def iterEdges(fun: (String)=> Unit, ids: String*): Sigma

    @native def getNodes(ids: String*): Array[Node]

    @native def getEdges(ids: String*): Array[Edge]

    // Monitoring
    /*def activateMonitoring {
        return s.monitor.activate();
    };*/

    /*def desactivateMonitoring {
        return s.monitor.desactivate();
    };*/

    @native def bind(eventName: String, eventProcedure: Unit => Unit): Sigma

    /**
     * Activates the Fish Eye feature of the Sigma.js plugin. Requires sigma.fisheye.js plugin library for Sigma.js
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def activateFishEye(): Sigma

    /**
     * Activates the Force positioning feature of the Sigma.js plugin. Requires sigma.forceatlas2.js plugin library for Sigma.js
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def startForceAtlas2()

    /**
     * Deactivates the Force positioning feature of the Sigma.js plugin. Requires sigma.forceatlas2.js plugin library for Sigma.js
     * @return instance of the Sigma plugin (chaining support)
     */
    @native def stopForceAtlas2()
}
