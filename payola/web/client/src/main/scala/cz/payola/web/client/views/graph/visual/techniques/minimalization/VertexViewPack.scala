package cz.payola.web.client.views.graph.visual.techniques.minimalization

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.visual.graph._
import scala.Some

/**
 * Representation of a VertexView object used in MinimalizationTechnique perform routine.
 * @param value contained vertexView
 * @param children vertices, that have an edge with this contained vertexView without parent
 * @param parent vertexView higher in the vertices tree structure
 */
class VertexViewPack(var value: VertexViewElement, var children: ListBuffer[VertexViewPack],
    var parent: Option[VertexViewPack])
{
    /**
     * Permutations of the children list, generated for the rotateChildren routine
     */
    private var permutations: Option[ListBuffer[ListBuffer[VertexViewPack]]] = None

    /**
     * Generator of permutations of the children list.
     * @param toPermute container of elements to generate permutations for
     * @return container with all possible permutations of the input parameter
     */
    private def generatePermutations(toPermute: ListBuffer[VertexViewPack]): ListBuffer[ListBuffer[VertexViewPack]] = {
        //does not depend on the type of elements in the input container

        var toReturn = ListBuffer[ListBuffer[VertexViewPack]]()
        if (toPermute.length <= 1) {
            toReturn += toPermute
        } else {
            generatePermutations(toPermute.drop(1)).foreach { perm =>

                var pointer = 0
                while (pointer <= perm.length) {
                    var pom = perm.drop(pointer)
                    pom += toPermute.head
                    pom ++= perm.dropRight(pointer)
                    toReturn += pom
                    pointer += 1
                }
            }
        }
        toReturn
    }

    /**
     * Permutes the children list. If the children list has already contained its all possible permutations true
     * is returned
     * @return true if all possible permutations have been tryed
     */
    def rotateChildren(): Boolean = {
        if (permutations == None) {
            //permutations not initialized yet
            permutations = Some(generatePermutations(children))
        }

        if (permutations.get.length == 1) {
            children = permutations.get.head //use the last permutation - it's the original one
            permutations = Some(generatePermutations(children)) //generate new permutations
            true
        } else {
            children = permutations.get.head
            permutations.get.remove(0)
            false
        }
    }

    /**
     * returns the right-most parent with children, that do not have any further children
     * right-most...child is on the right, if it is at the end of the children container
     * @return
     */
    def getLastParent: VertexViewPack = {
        //find topmost parent
        var topmostParent = this
        while (topmostParent.parent != None) {
            topmostParent = topmostParent.parent.get
        }

        //first get the last level of parents in the structure
        var levelPrevious = ListBuffer[VertexViewPack]()
        var level = topmostParent.getLevelSimple(0)
        var levelNext = topmostParent.getLevelSimple(1)
        var levelNumber = 2
        while (levelNext.length != 0) {
            levelPrevious = level
            level = levelNext
            levelNext = topmostParent.getLevelSimple(levelNumber)
            levelNumber += 1
        }

        //than find the last element with children
        var lastParent: VertexViewPack = topmostParent
        levelPrevious.foreach { ancestor =>
            if (ancestor.children.length != 0) {
                lastParent = ancestor
            }
        }

        lastParent
    }

    /**
     * Counts elements in list of lists. Helper function for Equals routine.
     * @param container to count elements of
     * @return count of elements inside two dimensional container
     */
    private def countElements(container: ListBuffer[ListBuffer[VertexViewPack]]): Int = {
        var counter = 0
        container.foreach { subContainer =>
            subContainer.foreach { element =>
                counter += 1
            }
        }

        counter
    }

    /**
     * Routine for getting this structure in linearized style...
     * (e.g. tree A-B, A-C, B-D, B-E, C-F, C-G is linearized like this: [A, B, C, D, E, F, G].
     * The previous brother of F is than E.)
     * n
     * n-1 <= n-2
     * n-3 <= n-4 <= n-5 <= n-6
     * @return
     */
    def getPreviousBrother: VertexViewPack = {
        //find topmost parent
        var topmostParent = this
        while (topmostParent.parent != None) {
            topmostParent = topmostParent.parent.get
        }

        //find the level in which this vertexViewPack is and return the previous element
        var level = ListBuffer[VertexViewPack](topmostParent)
        var levelNumber = 0
        var previousElement = topmostParent
        var gotPreviousBrother = false

        var result = topmostParent //in case that this == topmostParent
        while (!gotPreviousBrother) {
            level.foreach { element =>
                if ((element.value.isEqual(this.value)) && !gotPreviousBrother) {
                    result = previousElement
                    gotPreviousBrother = true
                }
                previousElement = element
            }

            level = topmostParent.getLevelSimple(levelNumber)
            levelNumber += 1
        }

        result
    }

    /**
     * Routine for getting this structure in linearized style...
     * (e.g. tree A-B, A-C, B-D, B-E, C-F, C-G is linearized like this: [A, B, C, D, E, F, G].
     * The previous brother of F is than E.)
     * n
     * n-1 <= n-2
     * n-3 <= n-4 <= n-5 <= n-6
     * @return
     */
    def getPreviousBrotherWithChildren: VertexViewPack = {
        var lastElement = this //if the graph consists of only 2 vertices
        var previousBrother = getPreviousBrother
        while (previousBrother.children.length < 2 &&
            (!(lastElement.value.isEqual(previousBrother.value)))) {

            lastElement = previousBrother
            previousBrother = previousBrother.getPreviousBrother
        }

        previousBrother
    }

    /**
     * creates a copy of this structure, but without copying its content of values
     * @return
     */
    override def clone(): VertexViewPack = {
        val root = new VertexViewPack(value, ListBuffer[VertexViewPack](), None)
        var level1New = ListBuffer[VertexViewPack](root)
        var level1This = ListBuffer[VertexViewPack](this)
        var level2New = ListBuffer[VertexViewPack]()
        var level2This = ListBuffer[VertexViewPack]()


        while (level1New.length != 0) {

            var pointerParent = 0
            level1New.foreach { actParentNew =>

                val actParentThis = level1This.apply(pointerParent)
                actParentThis.children.foreach { childThis =>

                    val childNew = new VertexViewPack(childThis.value, ListBuffer[VertexViewPack](), Some(actParentNew))
                    actParentNew.children += childNew

                    level2New += childNew
                    level2This += childThis
                }

                pointerParent += 1
            }

            level1New = level2New
            level2New = ListBuffer[VertexViewPack]()
            level1This = level2This
            level2This = ListBuffer[VertexViewPack]()
        }

        root
    }

    /**
     * Comparison of the structure of this object without recursion.
     * (with too deep recursion it might crash in JS (DAMN YOU JS!!!!))
     * @param pack to compare this object with
     * @return true if the structure is of both objects is the same
     */
    def isEqual(pack: VertexViewPack): Boolean = {
        var equal = true
        if (!(this.value.isEqual(pack.value))) {
            equal = false
        } else {
            var level = this.getLevel(0)
            var levelCompared = pack.getLevel(0)
            var levelNumber = 0

            var levelElementsCount = countElements(level)
            var levelComparedElementsCount = countElements(levelCompared)

            while ((levelElementsCount != 0 || levelComparedElementsCount != 0) && equal) {
                if (levelElementsCount != levelComparedElementsCount) {
                    equal = false
                } else {
                    var pointerList = 0
                    level.foreach { elementsThis =>
                        val elementsRecord = levelCompared(pointerList)
                        var pointerElement = 0

                        if (elementsRecord.length != elementsThis.length) {
                            equal = false
                        } else {
                            elementsThis.foreach { elementThis =>
                                val elementRecord = elementsRecord(pointerElement)

                                if (!(elementThis.value.isEqual(elementRecord.value))) {
                                    equal = false
                                }
                                pointerElement += 1
                            }
                        }
                        pointerList += 1
                    }
                }

                levelNumber += 1
                level = this.getLevel(levelNumber)
                levelCompared = pack.getLevel(levelNumber)

                levelElementsCount = countElements(level)
                levelComparedElementsCount = countElements(levelCompared)
            }
        }

        equal
    }

    /**
     * returns just a list of vertices in the specified level (no vertex repeates)
     * @param levelNum
     * @return
     */
    private def getLevelSimple(levelNum: Int): ListBuffer[VertexViewPack] = {
        var level = this.children
        var levelNext = ListBuffer[VertexViewPack]()
        var processed = ListBuffer[VertexViewPack](this)
        processed ++= this.children
        var currentLevel = 0

        while (currentLevel != levelNum) {

            level.foreach { parent =>

                parent.children.foreach { child =>
                    val found = processed.find(element => element.value.isEqual(child.value))
                    if (found == None) {
                        levelNext += child
                        processed += child
                    }
                }
            }

            level = levelNext
            levelNext = ListBuffer[VertexViewPack]()
            currentLevel += 1
        }

        level
    }

    /**
     * Builds container with elements with the same distance from this object.
     * The distance is measured by count of edges that create the shortest
     * path between vertices.
     * @param levelNum specifies number of edges, that create the shortest path
     * @return list of lists of vertices that are reachable by the minimum count
     *         of edges; every list describes children of a vertex in a previous level (levelNum -1)
     */
    def getLevel(levelNum: Int): ListBuffer[ListBuffer[VertexViewPack]] = {
        var result = ListBuffer[ListBuffer[VertexViewPack]](children)
        var level = ListBuffer[ListBuffer[VertexViewPack]](children)
        var levelNext = ListBuffer[VertexViewPack]()
        var currentLevel = 0
        while (currentLevel != levelNum) {

            result = ListBuffer[ListBuffer[VertexViewPack]]()
            level.foreach { listOfParents =>

                listOfParents.foreach { parent =>

                    var pom = ListBuffer[VertexViewPack]()
                    parent.children.foreach { child =>
                        val found = levelNext.find(element => element.value.isEqual(child.value))
                        if (found == None) {
                            levelNext += child
                        }

                        pom += child
                    }
                    result += pom
                }
            }

            level = ListBuffer[ListBuffer[VertexViewPack]]()
            level += levelNext
            levelNext = ListBuffer[VertexViewPack]()
            currentLevel += 1
        }

        result
    }
}
