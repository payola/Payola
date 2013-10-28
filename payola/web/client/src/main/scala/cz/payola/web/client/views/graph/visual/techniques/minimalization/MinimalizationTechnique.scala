package cz.payola.web.client.views.graph.visual.techniques.minimalization

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.algebra.Point2D
import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.models.PrefixApplier

class MinimalizationTechnique(prefixApplier: Option[PrefixApplier]) extends BaseTechnique("Tree ECM Visualization", prefixApplier)
{
    //TODO add some computation branch cutting...this algorithm is quite complex

    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {
        minimizeEdgeCrossing(component.vertexViewElements) //TODO this is impossible to combine with animation

        if (animate) {
            new Animation(basicTreeStructure, component.vertexViewElements, None, redrawQuick, redraw, None)
        } else {
            new Animation(basicTreeStructure, component.vertexViewElements, None, redrawQuick, redraw, Some(0))
        }
    }

    /**
     * Builds structure for easier vertexViews handling
     * @param rootVertexView
     * @return
     */
    private def buildUtilityStructure(rootVertexView: VertexViewElement): VertexViewPack = {
        val structureRoot = new VertexViewPack(rootVertexView, ListBuffer[VertexViewPack](), None)
        var level = ListBuffer[ListBuffer[VertexViewPack]](
            ListBuffer[VertexViewPack](structureRoot))
        var levelNext = ListBuffer[ListBuffer[VertexViewPack]]()
        var alreadyProcessed = ListBuffer[VertexViewElement](rootVertexView)
        //^list of vertices, for which a vertexViewPack was already created

        //in every whyle-cycle iteration is build a list of children to the level2Records with parent set parent
        // variable
        //and all parents contained in the level1Records have set their children variable. At the end of the while-cycle
        //the level1Records variable is replaced with the level2Records variable and level2Records is set empty
        while (level.length != 0) {
            level.foreach { parentList =>
                parentList.foreach { parent =>

                    var children = ListBuffer[VertexViewPack]()
                    parent.value.edges.foreach { parentsEdge =>

                        val child = if (parent.value.isEqual(parentsEdge.originView)) {
                            parentsEdge.destinationView
                        } else {
                            parentsEdge.originView
                        }
                        if (alreadyProcessed.find(element => element.isEqual(child)) == None) {

                            children += new VertexViewPack(child, ListBuffer[VertexViewPack](), Some(parent))
                        }
                    }

                    parent.children = children //set created children to the parent
                    levelNext += children
                    children = ListBuffer[VertexViewPack]() //clear the children variable for next cycle
                }
            }

            //moving to next level
            level = levelNext
            levelNext = ListBuffer[ListBuffer[VertexViewPack]]()

            //items in the level are marked as alreadyProcessed, that they won't be added to levelNext
            level.foreach { records =>
                records.foreach { record =>
                    alreadyProcessed += record.value
                }
            }
        }

        structureRoot
    }

    /**
     * Sorts lists of edges of every vertexView in the structure, that it is in the same order as in the
     * vertexViewPack.children container.
     * (example: VertexViewPack A has children B, C, D. The vertexView that A contains has edges to vertexViews
     * B, C, D, but the A.value.edges container is sorted like this: A-D, A-C, A-B. The sorted list of edges of
     * the A vertexView will be A-B, A-C, A-D.)
     * @param rootVertexViewPack
     */
    private def sortEdgeViewLists(rootVertexViewPack: VertexViewPack) {
        var level = ListBuffer[VertexViewPack](rootVertexViewPack)
        var levelNext = ListBuffer[VertexViewPack]()

        while (level.length != 0) {

            level.foreach { parent =>

                var orderedEdges = ListBuffer[EdgeView]()
                parent.children.foreach { parentsChild =>

                    val foundEdge = parent.value.edges.find { element =>
                        (element.originView.isEqual(parentsChild.value)) ||
                            (element.destinationView.isEqual(parentsChild.value))
                    }.get //finds edge in the parent.value.edges that connects parent.value and parentsChild.value

                    orderedEdges += foundEdge

                    levelNext += parentsChild
                }

                //if a grandparent exist and an edge to it in parent.edges container it is also added
                if (parent.parent != None) {
                    val edgeToGrandParent = parent.value.edges.find { element =>
                        (element.originView.isEqual(parent.parent.get.value)) ||
                            (element.destinationView.isEqual(parent.parent.get.value))
                    }
                    if (edgeToGrandParent != None) {
                        orderedEdges += edgeToGrandParent.get
                    }
                }

                parent.value.edges = orderedEdges
            }

            level = levelNext
            levelNext = ListBuffer[VertexViewPack]()
        }
    }

    /**
     * Level is list of vertexViews that are connected by one edge to a vertex in a previus level. The first level
     * contains one "special" (e.g. user-selected) edge.
     * The algorithm finds drawing of the graph that has the lowest count of edge crossings of edges between levels.
     * In every iteration the crossings are counted and chilren of the last ratate-able parent are rotated
     * (see rotate(..)). The found drawing is then set by the sorthEdgeViewList method.
     * @param vertexViews
     */
    private def minimizeEdgeCrossing(vertexViews: ListBuffer[VertexViewElement]) {
        val originalStructure = buildUtilityStructure(vertexViews.head)
        var minCrossings = Double.MaxValue
        var memory: VertexViewPack = originalStructure.clone() //best drawing so far
        val currentStructure = originalStructure.clone() //structure of a curret iteration
        var compute = true //iteration works until all possible rotations are tested or found drawing has zero crossings

        while (compute) {

            var actCrossings: Double = 0
            var level = currentStructure.getLevel(0)
            var levelNum = 1
            while (level.length != 0) {
                //count crossings in every level
                actCrossings += countCrossings(level)
                level = currentStructure.getLevel(levelNum)
                levelNum += 1
            }
            if (minCrossings > actCrossings) {
                minCrossings = actCrossings
                memory = currentStructure.clone()
            }
            rotate(currentStructure)

            compute = !(originalStructure.isEqual(currentStructure) || minCrossings == 0)
        }

        sortEdgeViewLists(memory)
    }

    /**
     * Finds parent of children, that do not have any further children (by vertexViewPack.getLastParent)
     * and rotates the children list (by vertexViewPack.rotateChildren). If the returned value is true
     * it takes previous brother of the (current) parent and rotates its children, etc.
     * (see vertexViewPack.getPreviousBrother)
     * @param root
     */
    private def rotate(root: VertexViewPack) {
        // 1) vezmu parenta posledniho children listu a zarotuju
        // 2) pokud pri rotaci doslo k otoceni permutace dokola (jsem opet na prvni permutaci)
        //      musim udelat permutaci i na predchozim bratrovi (pokud takovy neni rotuju otce meho posledniho bratra)
        var lastParent = root.getLastParent
        if (lastParent.children.length < 2) {
            lastParent = lastParent.getPreviousBrotherWithChildren
        }

        if (lastParent.rotateChildren()) {
            var brother = lastParent.getPreviousBrotherWithChildren

            while (brother.rotateChildren() && brother.parent != None) {
                //if brother.parent == None pak jsem dosel do korene struktury a vsechny rotace byly vyzkouseny
                brother = brother.getPreviousBrotherWithChildren
            }
        }
    }

    /**
     * Counts crossings in the input level, nothing more, nothing less
     * @param levelOfElements
     * @return
     */
    private def countCrossings(levelOfElements: ListBuffer[ListBuffer[VertexViewPack]]): Double = {
        var processedVertexGroups = ListBuffer[VertexViewPack]() //vertices that were present in already processed groups

        var crossings: Double = 0 //counted crossings in the level... result


        levelOfElements.foreach { vertexGroup =>
            vertexGroup.foreach { vertex =>

                val pom = countCrossingsIn(processedVertexGroups, vertex)

                if (pom > -1) {
                    crossings += pom
                }
            }

            processedVertexGroups ++= vertexGroup
        }

        crossings
    }

    /**
     * Support function for countCrossings.
     * Finds first appearance of addedElement in processedVertexGroups and counts elements, that did not appear
     * before addedElement (addedElement included), from this position to the end of the container.
     * Returns -1 if addedElement is not found or number of crossings
     * @param processedVertexGroups
     * @param addedElement
     * @return
     */
    private def countCrossingsIn(processedVertexGroups: ListBuffer[VertexViewPack],
        addedElement: VertexViewPack): Int = {
        var ignoreList = ListBuffer[VertexViewPack]()
        var pointer = 0
        var count = -1

        while (pointer < processedVertexGroups.length) {
            ignoreList += processedVertexGroups(pointer)
            if (processedVertexGroups(pointer).value.isEqual(addedElement.value)) {
                pointer += 1
                count = 0
                while (pointer < processedVertexGroups.length) {
                    val p = ignoreList.find(element =>
                        element.value.isEqual(processedVertexGroups(pointer).value))

                    if (p == None) {
                        count += 1
                    }
                    pointer += 1
                }
            }
            pointer += 1
        }

        count
    }
}
