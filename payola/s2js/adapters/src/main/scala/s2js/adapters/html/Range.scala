package s2js.adapters.html

import s2js.adapters.dom.Node

abstract class Range
{
    def insertNode(n: Node)

    def surroundContents(n: Node)
}
