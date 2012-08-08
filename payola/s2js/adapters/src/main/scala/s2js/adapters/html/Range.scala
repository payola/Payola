package s2js.adapters.html

import s2js.adapters.dom._

trait Range
{
    val startContainer: Node
    
    val startOffset: Int
    
    val endContainer: Node
    
    val endOffset: Int
    
    val collapsed: Boolean
    
    val commonAncestorContainer: Node

    def setStart(refNode: Node, offset: Int)
    
    def setEnd(refNode: Node, offset: Int)
    
    def setStartBefore(refNode: Node)
    
    def setStartAfter(refNode: Node)
    
    def setEndBefore(refNode: Node)
    
    def setEndAfter(refNode: Node)
    
    def collapse(toStart: Boolean)
    
    def selectNode(refNode: Node)
    
    def selectNodeContents(refNode: Node)
    
    def compareBoundaryPoints(how: Int, sourceRange: Range): Int
    
    def deleteContents()

    def extractContents(): DocumentFragment

    def cloneContents(): DocumentFragment

    def insertNode(node: Node)

    def surroundContents(newParent: Node)

    def cloneRange(): Range

    def detach()

    def isPointInRange(node: Node, offset: Int): Boolean

    def comparePoint(node: Node, offset: Int): Node

    def intersectsNode(node: Node): Boolean
}

object Range
{
    val START_TO_START = 0
    
    val START_TO_END = 1
    
    val END_TO_END = 2
    
    val END_TO_START = 3
}
