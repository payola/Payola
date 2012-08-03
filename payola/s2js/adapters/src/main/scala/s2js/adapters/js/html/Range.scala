package s2js.adapters.js.html

import s2js.adapters.js.dom.Node

abstract class Range
{
    def insertNode(n: Node)

    def surroundContents(n: Node)
}
