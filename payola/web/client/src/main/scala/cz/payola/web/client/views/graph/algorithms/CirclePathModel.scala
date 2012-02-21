package cz.payola.web.client.views.graph.algorithms

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.Point

class Record(var value: VertexView,var children: ListBuffer[Record], var parent: Record) {
    var childrenRotated = 0

    /**
      * returns true if all children have been rotated (the originally first child is at the first position again)
      * @return
      */
    def rotateChildren(): Boolean = {
        var rotatedCircle = false

        if(children.length != 0) {
            val rotatedChild = children.remove(0)
            children += rotatedChild
            childrenRotated += 1

            if(childrenRotated == children.length) {
                childrenRotated = 0
                rotatedCircle = true
            }
        }

        rotatedCircle
    }

    /**
      * returns the right-most record with children, that do not have any further children
      * @return
      */
    def getLastParent(): Record = {
        var pointer = this
        while (pointer.children.length != 0) {
            pointer = pointer.children.last
        }

        pointer.parent
    }

    /**
      * Routine for geting Records in linearized style...
      * n
      * n-1 <= n-2
      * n-3 <= n-4 <= n-5 <= n-6 ...
      * @return
      */
    def getPreviousBrother(): Record = {

        var result: Record = null
        if(parent != null){
            var previous: Record = null
            var finished = false
            parent.children.foreach{ bro =>
                if(!finished) {
                    if(bro.value.vertexModel.uri == value.vertexModel.uri) {
                        result = previous
                        finished = true
                        result //no need to continue the cycle
                    } else {
                        previous = bro
                    }
                }
            }

            if(result == null) { //this record is in the first one in its parents children list
                if(parent.parent == null) { // this is the top of the tree
                    result = parent
                } else {
                    val previous = getPrevious(this, parent.parent.children)
                    if(previous == null) { //this is the leftmost branch of the tree
                        result = parent.parent.children.last
                    } else {
                        result = previous.children.last
                    }
                }
            }
        }

        result
    }

    private def getPrevious(element: Record, container: ListBuffer[Record]): Record = {
        var previous: Record = null
        container.foreach{ record =>
            if(record.value.vertexModel.uri == element.value.vertexModel.uri) {
                return previous
            }

            previous = record
        }

        null
    }

    /**
      * comparing wihout recursion...with too deep recursion it might crash in JS (DAMN YOU JS!!!!)
      * @param record
      * @return
      */
    def isEqual(record: Record): Boolean = {

        var equal = true
        if(this.value.vertexModel.uri != record.value.vertexModel.uri) {
            equal = false
        } else {
            var level1this = ListBuffer[ListBuffer[Record]](this.children)
            var level2this = ListBuffer[ListBuffer[Record]]()
            var level1Record = ListBuffer[ListBuffer[Record]](record.children)
            var level2Record = ListBuffer[ListBuffer[Record]]()

            while(level1this.length != 0 && equal) {
                if(level1this.length == level1Record.length) {
                    var pointerList = 0
                    if(equal){
                        level1this.foreach{ elementsThis =>
                            val elementsRecord = level1Record(pointerList)
                            var pointerElement = 0

                            if(equal){
                                elementsThis.foreach{ elementThis =>
                                    val elementRecord = elementsRecord(pointerElement)

                                    if(elementThis.value.vertexModel.uri != elementRecord.value.vertexModel.uri) {
                                        equal = false
                                    }

                                    level2this += elementThis.children
                                    level2Record += elementRecord.children
                                    pointerElement += 1
                                }
                            }
                            pointerList += 1
                        }
                    }
                } else {
                    equal = false
                }

                level1this = level2this
                level2this = ListBuffer[ListBuffer[Record]]()
                level1Record = level2Record
                level2Record = ListBuffer[ListBuffer[Record]]()
            }
        }

        equal
    }
}

class CirclePathModel extends AlgoBase {

    def perform(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {

        val plk = minimizeEdgeCrossing(vertexViews)
        treeLikeVerticesPositioning(plk)
        //TODO use the run routine to draw vertices in circle
        //run(vertexViews, edgeViews)
        moveGraphToUpperLeftCorner(vertexViews)
    }

    private def buildToStructure(rootVertexView: VertexView): Record = {
        if(rootVertexView == null) {
            return null
        }

        val resultRoot = new Record(rootVertexView, null, null)
        var level1Records = ListBuffer[ListBuffer[Record]](ListBuffer[Record](resultRoot))
        var level2Records = ListBuffer[ListBuffer[Record]]()
        var children = ListBuffer[Record]()
        var alreadyProcessed = ListBuffer[VertexView](rootVertexView)

        //in every whyle-cycle iteration is build a list of children to the level2Records with parent set parent variable
        //and all parents contained in the level1Records have set their children variable. At the end of the while-cycle
        //the level1Records variable is replaced with the level2Records variable and level2Records is set empty
        while(level1Records.length != 0) {
            level1Records.foreach{ records =>
                records.foreach{ recordParent =>
                    recordParent.value.edges.foreach{ edgeView =>

                        val child = if(recordParent.value.vertexModel.uri == edgeView.originView.vertexModel.uri) {
                            edgeView.destinationView
                        } else {
                            edgeView.originView
                        }
                        if(!existsVertex(child, alreadyProcessed)) {

                            children += new Record(child, null, recordParent)
                        }
                    }

                    recordParent.children = children
                    level2Records += children
                    children = ListBuffer[Record]()
                }
            }

            level1Records = level2Records
            level2Records = ListBuffer[ListBuffer[Record]]()

            level1Records.foreach{ records =>
                records.foreach{ record =>
                    alreadyProcessed += record.value
                }
            }
        }

        resultRoot
    }

    private def buildFromStructure(r: Record): ListBuffer[VertexView] = {
        if(r == null) {
            return null
        }

        var result = ListBuffer[VertexView]()
        var level1 = ListBuffer[Record](r)
        var level2 = ListBuffer[Record]()

        while(level1.length != 0) {
            level1.foreach{ record =>
                result += record.value

                record.children.foreach{ childRecord =>
                    if(!level2.exists(element => element.value.vertexModel.uri == childRecord.value.vertexModel.uri)) {
                        level2 += childRecord
                    }
                }
            }

            level1 = level2
            level2 = ListBuffer[Record]()
        }

        result
    }



    private def minimizeEdgeCrossing(vs: ListBuffer[VertexView]): ListBuffer[VertexView] = {
        val origin = buildToStructure(vs.head)
        var minCrossings = Double.MaxValue
        var memory: Record = null
        var actStruc = origin
        var work = true

        while(work) {
            var actCrossings: Double = 0
            var level = getLevel(origin, 0)
            var levelNum = 1
            while(level.length != 0) {
                actCrossings += countCrossings(level)
                level = getLevel(origin, levelNum)
                levelNum += 1
            }
            if(minCrossings > actCrossings) {
                minCrossings = actCrossings
                memory = actStruc
            }
            rotate(actStruc)

            work = !actStruc.isEqual(origin)
        }

        buildFromStructure(memory) //return
    }

    /**
      * level 0 means direct children of the origin
      * @param origin
      * @param levelNum
      * @return
      */
    private def getLevel(origin: Record, levelNum: Int): ListBuffer[ListBuffer[Record]] = {
        var level1 = ListBuffer[ListBuffer[Record]](origin.children)
        var level2 = ListBuffer[ListBuffer[Record]]()

        var pointer = 0
        while(pointer != levelNum) {
            level1.foreach{ listOfElements =>
                listOfElements.foreach{ element =>
                    level2 += element.children
                }
            }

            level1 = level2
            level2 = ListBuffer[ListBuffer[Record]]()
            pointer += 1
        }

        level1
    }

    private def rotate(struct: Record) {
        // 1) vezmu parenta posledniho children listu a zarotuju
        // 2) pokud pri rotaci doslo k otoceni permutace dokola (jsem opet na prvni permutaci)
        //      musim udelat permutaci i na predchozim bratrovi (pokud takovy neni rotuju otce meho posledniho bratra)
        val lastParent = struct.getLastParent()

        if(lastParent.rotateChildren()) {
            var previousBrother = lastParent.getPreviousBrother()
            while(previousBrother != null && previousBrother.rotateChildren()) {
                previousBrother = previousBrother.getPreviousBrother()
            }
        }
    }

    /**
      * WARNING this will fail if between two vertices are multiple edges!!!!!!
      * Counts crossings in the input level, nothing more, nothing less
      * @param levelOfElements
      * @return
      */
    private def countCrossings(levelOfElements: ListBuffer[ListBuffer[Record]]): Double = {
        var records = ListBuffer[Record]() //container where position of alredy processed vertices (!, not records) in the level can be found
        var crossings: Double  = 0 //result

        var previousRecordsLength = 0
        levelOfElements.foreach{ children =>
            val elements = ListBuffer[Record]()
            children.foreach{ child =>

                val position = records.findIndexOf(element => child.value.vertexModel.uri == element.value.vertexModel.uri)
                records += child

                if(position > -1) {
                    crossings += previousRecordsLength - 1 - position
                }



                elements ++= child.children
            }
            previousRecordsLength += children.length
        }

        crossings
    }

    private def run(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {

        var level1 = ListBuffer[VertexView]()
        var level2 = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()
        var levelNum = 0

        level1 += vertexViews.head

        while(level1.length != 0) {

            placeVerticesOnCircle(levelNum*3, levelNum*100, vertexViews.head.position, level1)

            level1.foreach{ l1: VertexView =>
                l1.edges.foreach{ e: EdgeView =>
                    if(e.originView.vertexModel.uri == l1.vertexModel.uri) {
                        if(!existsVertex(e.destinationView, alreadyOut)
                            && !existsVertex(e.destinationView, level2) && !existsVertex(e.destinationView, level1)) {
                            level2 += e.destinationView
                            val i = 0
                        }
                    } else {
                        if(!existsVertex(e.originView, alreadyOut)
                            && !existsVertex(e.originView, level2) && !existsVertex(e.originView, level1)) {
                            level2 += e.originView
                            val i = 0
                        }
                    }
                }
                alreadyOut += l1
            }

            level1 = ListBuffer[VertexView]()
            level1 = level2
            level2 = ListBuffer[VertexView]()
            levelNum += 1
        }
    }

    private def placeVerticesOnCircle(rotation: Double, radius: Double, center: Point, vertexViews: ListBuffer[VertexView]) {
        val angle = 360 / vertexViews.length

        var counter = 0
        var angleAct: Double = 0
        var x: Double = 0
        var D: Double = 0
        var y1: Double = 0
        var y2: Double = 0
        vertexViews.foreach{ vertexView =>

            angleAct = angle*counter + rotation
            if (angleAct > 360) {
                angleAct -= 360
            }
            x = center.x + radius * scala.math.cos(angleAct*scala.math.Pi/180)
            //don't ask me why, but the function expects the parameter in radians
            if(center.x - x <= radius) {
                D = scala.math.sqrt(-scala.math.pow(center.x, 2) + 2*center.x +
                    scala.math.pow (radius, 2) - scala.math.pow(x, 2))
                y1 = center.y - D
                y2 = center.y + D

                if(0 <= angleAct && angleAct < 90) {
                    vertexView.position = Point(x, y1)
                } else if(90 <= angleAct && angleAct < 180) {
                    vertexView.position = Point(x, y1)
                } else if(180 <= angleAct && angleAct < 270) {
                    vertexView.position = Point(x, y2)
                } else if(270 <= angleAct && angleAct < 360) {
                    vertexView.position = Point(x, y2)
                }
            }


            counter += 1
        }
    }
}
